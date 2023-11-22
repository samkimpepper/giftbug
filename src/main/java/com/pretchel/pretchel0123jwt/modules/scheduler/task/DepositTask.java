package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositRepository;
import com.pretchel.pretchel0123jwt.infra.OpenbankingApi;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositService;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.DepositResultCheckResponseDto;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.scheduler.test.DepositTaskTestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositTask {
    private final GiftRepository giftRepository;

    private final DepositTaskTransactional depositTaskTransactional;

    @Value("${openbanking.deposit.bank-tran-id}")
    private String bankTranId;
    private static final Random RANDOM = new Random();

    private final GiftService giftService;
    private final OpenbankingApi openbankingApi;
    private final OpenbankingDepositService openbankingDepositService;

    /*
     * 입금이체 에러 처리
     * 400, 803, 804: 그냥 에러.
     * 822: 은행거래고유번호 중복. 다시 만들어서 해야됨;; -> 이것만 입금이체 재요청.
     * A0007: 에러.
     * 400, 803, 804, 822, A0007일 시 OpenbankingStatus는 unchecked로
     * 그 이외는 failed로.
     * */
    public void depositExpiredGiftAmount() {
        List<Gift> gifts = giftRepository.findExpiredGiftFetchJoin(GiftState.expired, ProcessState.none);

        List<CompletableFuture<Void>> futures = gifts.stream()
                .map(gift -> CompletableFuture.runAsync(() -> depositExpiredGiftAmountInternal(gift)))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public void depositExpiredGiftAmountInternal(Gift gift) {
        // User
        Users receiver = gift.getEvent().getUsers();

        // Account
        Account receiverAccount = receiver.getDefaultAccount();

        // 사전 저장
        String newBankTranId = bankTranId;
        newBankTranId = newBankTranId.concat(generateRandomString());
        String finalNewBankTranId = newBankTranId;
        CompletableFuture<OpenbankingDeposit> depositFuture = CompletableFuture
                .supplyAsync(() -> openbankingDepositService.preSave(finalNewBankTranId, gift, receiver))
                .exceptionally(ex -> {
                    log.error("preSave error:" + ex);
                    log.info("preSave()가 실패했으므로 종료");
                    return null;
                });


        // 입금이체 실행
        CompletableFuture<OpenbankingDepositResponseDto> responseFuture = CompletableFuture
                .supplyAsync(() -> openbankingApi.depositAmount(String.valueOf(gift.getFunded()), receiverAccount.getName(), receiverAccount.getBankCode(), receiverAccount.getAccountNum()));

        depositFuture.thenAcceptBoth(responseFuture, (deposit, response) -> {
            // api 응답 코드와 은행 응답 코드 두 개가 있음.
            String apiRspCode = response.getRsp_code();
            String bankRspCode = response.getRes_list().get(0).getBank_rsp_code();
            String rspMsg = response.getRsp_message();
            log.info("오픈뱅킹 입금이체 API 응답 코드: " + apiRspCode);
            log.info("오픈뱅킹 입금이체 참가은행 응답 코드: " + bankRspCode);
            log.info("오픈뱅킹 입금이체 응답 메세지: " + rspMsg);

            if(!response.getRes_list().isEmpty() && bankRspCode.equals("000")) {
                depositTaskTransactional.postProcess(gift, ProcessState.completed, deposit, OpenbankingStatus.PAID, response);
                return;
            }
            if(apiRspCode.equals("A0007") ||bankRspCode.equals("400") || bankRspCode.equals("803")|| bankRspCode.equals("804") || bankRspCode.equals("822")){
                depositTaskTransactional.postProcess(gift, ProcessState.check, deposit, OpenbankingStatus.UNCHECKED, response);
                return;
            }

            depositTaskTransactional.postProcess(gift, ProcessState.none, deposit, OpenbankingStatus.FAILED, response);

        }).join();
    }

    private String generateRandomString() {
        int len = 9;
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int alphaLen = alpha.length();

        StringBuffer code = new StringBuffer();
        for(int i = 0; i < len; i++) {
            code.append(alpha.charAt(RANDOM.nextInt(alphaLen)));
        }
        // ThreadLocalRandom.current().nextInt();

        return code.toString();
    }

//    @Transactional
//    public void depositExpiredGiftAmount() {
//        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.none);
//
//        for(Gift gift: gifts) {
//            depositTaskTransactional.depositExpiredGiftAmountTransactional(gift);
//        }
//    }

    // 로직 순서대로 설명
    /*
    * 1. Gift 중, 만료되었고(expired) 잘 처리되었는지 확인해야하는(check) 것만 조회해서 순회함.
    * 2. Gift 각각에 대해, 해당 Gift에 달린 입금이체 내역들(deposits)을 조회해옴.
    * 3. 그런데, 내역들을 최근에 만들어진 순서대로 정렬해서 가져오기 때문에 맨 첫 번째 입금이체 엔티티만 확인할 거임.
    * 4.
    * */
    public void checkDepositResult() {
        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.check);

        for(Gift gift: gifts) {
            depositTaskTransactional.checkDepositResult(gift);
        }
    }

}
