package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import lombok.Getter;

@Getter
public class CheckReqListDto {
    private String tran_no;
    private String org_bank_tran_id;
    private String org_bank_tran_date;
    private int org_tran_amt;

    public CheckReqListDto(OpenbankingDeposit payment) {
        tran_no = payment.getTran_no();
        org_bank_tran_id = payment.getBank_tran_id();
        org_bank_tran_date = payment.getTran_dtime().substring(0, 9);
        org_tran_amt = payment.getAmount();
    }
}
