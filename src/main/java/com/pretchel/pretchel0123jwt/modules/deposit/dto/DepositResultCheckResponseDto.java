package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DepositResultCheckResponseDto {
    private String api_tran_id;
    private String api_tran_dtm;
    private String rsp_code;
    private String rsp_message;
    private int res_cnt;
    private List<CheckResListDto> res_list;
}
