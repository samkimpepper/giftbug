package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.infra.OpenbankingApi;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositService;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.DepositResultCheckResponseDto;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositTaskTransactional {
    @Value("${openbanking.deposit.bank-tran-id}")
    private String bankTranId;
    private static final Random RANDOM = new Random();

    private final GiftService giftService;
    private final OpenbankingApi openbankingApi;
    private final OpenbankingDepositService openbankingDepositService;

    public void depositExpiredGiftAmountTransactional(Gift gift) {
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
                postProcess(gift, ProcessState.completed, deposit, OpenbankingStatus.PAID, response);
                return;
            }
            if(apiRspCode.equals("A0007") ||bankRspCode.equals("400") || bankRspCode.equals("803")|| bankRspCode.equals("804") || bankRspCode.equals("822")){
                postProcess(gift, ProcessState.check, deposit, OpenbankingStatus.UNCHECKED, response);
                return;
            }

            postProcess(gift, ProcessState.none, deposit, OpenbankingStatus.FAILED, response);

        }).join();
    }

    @Transactional
    public void postProcess(Gift gift, ProcessState state, OpenbankingDeposit deposit, OpenbankingStatus status, OpenbankingDepositResponseDto response) {
        openbankingDepositService.postProcess(deposit, status, response);
        giftService.setProcessState(gift, state);
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void depositExpiredGiftAmountTransactional(Gift gift) {
//        // User
//        Users receiver = gift.getEvent().getUsers();
//
//        // Account
//        Account receiverAccount = receiver.getDefaultAccount();
//
//        // 사전 저장
//        String newBankTranId = bankTranId;
//        newBankTranId = newBankTranId.concat(generateRandomString());
//        OpenbankingDeposit deposit = openbankingDepositService.preSave(newBankTranId, gift, receiver);
//        if (deposit == null) {
//            // preSave()가 실패했으므로 종료
//            log.info("preSave()가 실패했으므로 종료");
//            return;
//        }
//
//        // 입금이체 실행
//        OpenbankingDepositResponseDto response = openbankingApi.depositAmount(String.valueOf(gift.getFunded()), receiverAccount.getName(), receiverAccount.getBankCode(), receiverAccount.getAccountNum());
//
//        // api 응답 코드와 은행 응답 코드 두 개가 있음.
//        String apiRspCode = response.getRsp_code();
//        String bankRspCode = response.getRes_list().get(0).getBank_rsp_code();
//        String rspMsg = response.getRsp_message();
//        log.info("오픈뱅킹 입금이체 API 응답 코드: " + apiRspCode);
//        log.info("오픈뱅킹 입금이체 참가은행 응답 코드: " + bankRspCode);
//        log.info("오픈뱅킹 입금이체 응답 메세지: " + rspMsg);
//
//        if(!response.getRes_list().isEmpty() && bankRspCode.equals("000")) {
//            openbankingDepositService.postProcess(deposit, OpenbankingStatus.PAID, response);
//            gift.completeProcess();
//            return;
//        }
//        if(apiRspCode.equals("A0007") ||bankRspCode.equals("400") || bankRspCode.equals("803")|| bankRspCode.equals("804") || bankRspCode.equals("822")){
//            openbankingDepositService.postProcess(deposit, OpenbankingStatus.UNCHECKED, response);
//            gift.shouldCheckProcess();
//            return;
//        }
//
//        openbankingDepositService.postProcess(deposit, OpenbankingStatus.FAILED, response);
//    }

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


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkDepositResult(Gift gift) {
        List<OpenbankingDeposit> deposits = openbankingDepositService.findAllByGiftDESC(gift);
        OpenbankingDeposit deposit = deposits.get(0);
        DepositResultCheckResponseDto response = openbankingApi.depositResultCheck(deposit);
        String rspCode = response.getRes_list().get(0).getBank_rsp_code();

        // 성공
        if(rspCode.equals("000")) {
            gift.completeProcess();
            deposit.success();
            return;
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
