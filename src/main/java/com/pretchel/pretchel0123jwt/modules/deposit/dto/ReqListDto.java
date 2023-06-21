package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReqListDto {
    private String tran_no;
    private String bank_tran_id;
    private String bank_code_std;
    private String account_num;
    private String account_holder_name;
    private String print_content;
    private String tran_amt;
    private String req_client_name;
    private String req_client_bank_code;
    private String req_client_account_num;
    private String req_client_num;
    private String transfer_purpose;


}
