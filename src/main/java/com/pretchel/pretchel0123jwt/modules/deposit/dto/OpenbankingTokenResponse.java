package com.pretchel.pretchel0123jwt.modules.deposit.dto;

import lombok.Getter;

@Getter
public class OpenbankingTokenResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;
    private String client_use_code;
}
