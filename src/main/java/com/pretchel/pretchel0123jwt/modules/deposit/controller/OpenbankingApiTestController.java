package com.pretchel.pretchel0123jwt.modules.deposit.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.infra.OpenbankingApi;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.DepositTestDto;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Api(value = "오픈뱅킹 입금 테스트")
public class OpenbankingApiTestController {
    @Value("${openbanking.client-id}")
    private String clientId;

    @Value("${openbanking.redirect-uri}")
    private String redirectUri;

    @Value("${openbanking.response-type}")
    private String responseType;

    @Value("${openbanking.scope}")
    private String scope;

    @Value("${openbanking.state}")
    private String state;

    @Value("${openbanking.auth-type}")
    private String authType;

    @Value("${openbanking.code-request-url}")
    private String codeRequestUrl;

    private final OpenbankingApi openbankingApi;

    @GetMapping("/test/openbanking/token")
    public ResponseDto.Data<String> testGetToken() {
        String token = openbankingApi.getToken();
        return new ResponseDto.Data<>(token);
    }

    // 요청 바디에 있는 금액으로 입금이체 해보는 테스트
    @PostMapping("/test/openbanking/deposit")
    public ResponseDto.Data<OpenbankingDepositResponseDto> testDeposit(@RequestBody DepositTestDto dto) {
        String token = openbankingApi.getToken();
        return new ResponseDto.Data<>(openbankingApi.depositAmount(dto.getAmount(), dto.getReqClientName(), dto.getReqClientBankCode(), dto.getReqClientAccountNum()));
    }


}
