package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.infra.OpenbankingApi;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositService;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.DepositResultCheckResponseDto;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositTaskTransactional {
    private final GiftRepository giftRepository;
    private final OpenbankingApi openbankingApi;


    private final OpenbankingDepositService openbankingDepositService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void depositExpiredGiftAmountTransactional(Gift gift) {
        // User
        Users receiver = gift.getEvent().getUsers();

        // Account
        Account receiverAccount = receiver.getDefaultAccount();

        // 입금이체 실행
        OpenbankingDepositResponseDto response = openbankingApi.depositAmount(String.valueOf(gift.getFunded()), receiverAccount.getName(), receiverAccount.getBankCode(), receiverAccount.getAccountNum());

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
            return;
        }
        if(apiRspCode.equals("A0007") ||bankRspCode.equals("400") || bankRspCode.equals("803")|| bankRspCode.equals("804") || bankRspCode.equals("822")){
                openbankingDepositService.save(OpenbankingStatus.UNCHECKED, response, gift, receiver);
                gift.shouldCheckProcess();
                return;
        }

        openbankingDepositService.save(OpenbankingStatus.FAILED, response, gift, receiver);
    }

    @Transactional
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
