package com.pretchel.pretchel0123jwt.modules.deposit.dto.ret;

import lombok.Getter;

@Getter
public class ReturnDepositRequest {
    private String bank_tran_id;
    private String org_bank_tran_date;
    private String org_bank_tran_id;
    private String org_dps_bank_code_std;
    private String org_dps_account_num;
    private int org_tran_amt;
    private String org_wd_bank_code_std;

    /*
    * 청구사유코드
    * 02: 계좌입력오류
    * 03: 금액입력오류
    * 04: 이중입금
    * 05: 기타(청구사유 필드 반드시 세팅)
    * 08: 고객 반환의사 표시로 재청구
    * */
    private int claim_code;
    private String total_return_yn;
    private String return_account_num;
    private String use_org_contact;
    private String use_org_email;
}
