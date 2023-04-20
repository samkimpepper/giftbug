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
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositTask {
    private final GiftRepository giftRepository;
    private final OpenbankingApi openbankingApi;

    private final OpenbankingDepositRepository depositRepository;

    private final OpenbankingDepositService openbankingDepositService;

    private final UserRepository userRepository;


    /*
     * 입금이체 에러 처리
     * 400, 803, 804: 그냥 에러.
     * 822: 은행거래고유번호 중복. 다시 만들어서 해야됨;; -> 이것만 입금이체 재요청.
     * A0007: 에러.
     * 400, 803, 804, 822, A0007일 시 OpenbankingStatus는 check로
     * 그 이외는 failed로.
     * */
    @Transactional
    public void depositExpiredGiftAmount() {
        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.none);

        String accessToken = openbankingApi.getToken();

        for(Gift gift: gifts) {
            // 펀딩 금액이 1원 이하, 즉 0원이면 바로 ProcessState를 completed로 두고
            // CompletedGift로 옮겨지도록 함.
            if(gift.getFunded() < 1) {
                gift.completeProcess();
                continue;
            }

            // TODO: 이부분 확인 필요. 그 사이에 선물 소유 유저가 탈퇴했으면 어떡하지?
            // 펀딩중인 선물이 있으면, 그걸 모두 강제로 마감하기 전까지 탈퇴할 수 없다고 먼저 말해줘야할 듯.
            // 선물 소유 유저가 없을 확률은 일단 없다고 가정하긴 하는데...
            // User
            Optional<Users> optionalUser = userRepository.findById(gift.getEvent().getUsers().getId());
            if(optionalUser.isEmpty()) continue;
            Users receiver = optionalUser.get();

            // Account
            Account receiverAccount = receiver.getDefaultAccount();

            // 입금이체
            OpenbankingDepositResponseDto response = openbankingApi.depositAmount(accessToken, String.valueOf(gift.getFunded()), receiverAccount.getName(), receiverAccount.getBankCode(), receiverAccount.getAccountNum());

            // api 응답 코드와 은행 응답 코드 두 개가 있음.
            String apiRspCode = response.getRsp_code();
            String bankRspCode = response.getRes_list().get(0).getBank_rsp_code();
            String rspMsg = response.getRsp_message();
            log.info("오픈뱅킹 입금이체 API 응답 코드: " + apiRspCode);
            log.info("오픈뱅킹 입금이체 참가은행 응답 코드: " + bankRspCode);
            log.info("오픈뱅킹 입금이체 응답 메세지: " + rspMsg);

            if(!response.getRes_list().isEmpty() && bankRspCode.equals("000")) {
                openbankingDepositService.save(OpenbankingStatus.PAID, response, gift, receiver);
                gift.completeProcess();
                continue;
            }
            switch (bankRspCode) {
                case "400": case "803": case "804": case "822":
                    openbankingDepositService.save(OpenbankingStatus.UNCHECKED, response, gift, receiver);
                    gift.shouldCheckProcess();
                    continue;
            }

            if(apiRspCode.equals("A0007")) {
                openbankingDepositService.save(OpenbankingStatus.UNCHECKED, response, gift, receiver);
                gift.shouldCheckProcess();
                continue;
            }

            openbankingDepositService.save(OpenbankingStatus.FAILED, response, gift, receiver);
        }
    }

    public void depositExpiredGiftAmountTransactional() {
        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.none);

        String accessToken = openbankingApi.getToken();
        for(Gift gift: gifts) {
            Optional<Users> optionalUser = userRepository.findById(gift.getEvent().getUsers().getId());
            if(optionalUser.isEmpty()) continue;
            Users receiver = optionalUser.get();

            // Deposit 엔티티 사전 저장
            String bankTranId = openbankingApi.generateBankTranID();
            openbankingDepositService.preSave(bankTranId, gift, receiver);
        }

    }

    // 로직 순서대로 설명
    /*
    * 1. Gift 중, 만료되었고(expired) 잘 처리되었는지 확인해야하는(check) 것만 조회해서 순회함.
    * 2. Gift 각각에 대해, 해당 Gift에 달린 입금이체 내역들(deposits)을 조회해옴.
    * 3. 그런데, 내역들을 최근에 만들어진 순서대로 정렬해서 가져오기 때문에 맨 첫 번째 입금이체 엔티티만 확인할 거임.
    * 4.
    * */
    @Transactional
    public void checkDepositResult() {
        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.check);


        String accessToken = openbankingApi.getToken();

        for(Gift gift: gifts) {
            List<OpenbankingDeposit> deposits = openbankingDepositService.findAllByGiftDESC(gift);
            OpenbankingDeposit deposit = deposits.get(0);
            DepositResultCheckResponseDto response = openbankingApi.depositResultCheck(deposit, accessToken);
            String rspCode = response.getRes_list().get(0).getBank_rsp_code();

            // 성공
            if(rspCode.equals("000")) {
                gift.completeProcess();
                deposit.success();
                continue;
            }

            // 실패 1) 나중에 입금이체 재요청
            // (바로 할까 아니면 다음날에 할까)
            if(rspCode.equals("701") || rspCode.equals("813")) {
                gift.shouldReDeposit();
                deposit.fail();
            }

            // 실패 2) 나중에 이체결과조회 재요청
        }
    }

    @Transactional
    public void checkDepositResult2() {
        List<OpenbankingDeposit> deposits = openbankingDepositService.findAllByStatus(OpenbankingStatus.UNCHECKED);

        for(OpenbankingDeposit deposit: deposits) {
            
        }
    }
}
