package com.pretchel.pretchel0123jwt.modules.payments.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class MessageTestCreateDto {

    @NotBlank
    private String nickname;

    private String content;

    @JsonProperty("paid_amount")
    @NotBlank
    private int paidAmount;


    @JsonProperty("gift_id")
    @NotBlank
    private String giftId;
}
