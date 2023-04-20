package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DepositResultCheckDto {
    private String check_type = "1";
    private String tran_dtime;
    private int req_cnt = 25;
    private List<CheckReqListDto> req_list;

    public DepositResultCheckDto(OpenbankingDeposit payments, CheckReqListDto dto) {
        tran_dtime = payments.getTran_dtime();
        req_list = new ArrayList<>();
        req_list.add(dto);
    }
}
