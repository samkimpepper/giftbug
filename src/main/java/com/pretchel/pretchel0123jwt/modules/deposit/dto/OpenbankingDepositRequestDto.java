package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OpenbankingDepositRequestDto {
    private String cntr_account_type;
    private String cntr_account_num;
    private String wd_pass_phrase;
    private String wd_print_content;
    private String name_check_option;
    private String tran_dtime;
    private String req_cnt;
    private List<ReqListDto> req_list;



}
