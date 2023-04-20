package com.pretchel.pretchel0123jwt.modules.deposit.dto.task;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DepositTaskRequest {
    private String bankTranId;
    private String amount;
    private String reqClientName;
    private String reqClientBankCode;
    private String reqClientAccountNum;

    public static DepositTaskRequest of(Gift gift, Account receiverAccount, String bankTranId) {
        return DepositTaskRequest.builder()
                .bankTranId(bankTranId)
                .amount(String.valueOf(gift.getFunded()))
                .reqClientName(receiverAccount.getName())
                .reqClientBankCode(receiverAccount.getBankCode())
                .reqClientAccountNum(receiverAccount.getAccountNum())
                .build();
    }
}
