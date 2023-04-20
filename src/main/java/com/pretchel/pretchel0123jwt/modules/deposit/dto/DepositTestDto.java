package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import lombok.Getter;

@Getter
public class DepositTestDto {
    private String amount;
    private String reqClientName;
    private String reqClientBankCode;
    private String reqClientAccountNum;

    // 이거 코드 보니까, 이 변수는 안 쓰이고 그냥 랜덤 문자열 만들어서 보냄. 만들고 안 지운듯.
    private String reqClientNum; // 이게 뭔지 모르겠어

}
