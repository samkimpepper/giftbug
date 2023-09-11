package com.pretchel.pretchel0123jwt.modules.payments.iamport.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(value= PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentsCompleteDto {

    private String impUid;
    private String merchantUid;
    private int amount;
    private String buyerName;
    private String message;
    private Boolean isMember;
    private String buyerEmail; // TODO: 이 부분 주의. 결제창에
    // 입력한 이메일과 우리 서비스에 가입한 이메일이 다르면 어떡하지?
    // 이걸, 유저가 입력한 이메일 말고, 프론트 쪽에서
    // 결제 시도한, 현재 유저의 이메일을 보내는 걸로 하자고 하자...
    // 그럼 buyerEmail 말고 다른 이름으로 해야될듯.
    private String giftId;

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }
}
