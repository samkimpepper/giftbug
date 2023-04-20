package com.pretchel.pretchel0123jwt.infra;

import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenbankingApi {
    // Token
    @Value("${openbanking.token-request-url}")
    private String requestUrl;

    @Value("${openbanking.client-id}")
    private String clientId;

    @Value("${openbanking.client-secret}")
    private String clientSecret;

    @Value("${openbanking.scope}")
    private String scope;

    @Value("${openbanking.grant-type}")
    private String grantType;

    // Deposit
    @Value("${openbanking.deposit.cntr-account-num}")
    private String cntrAccountNum;

    @Value("${openbanking.deposit.bank-tran-id}")
    private String bankTranId;

    @Value("${openbanking.deposit.bank-code-std}")
    private String bankCodeStd;

    @Value("${openbanking.deposit.account-holder-name}")
    private String accountHolderName;

    private final RestTemplate restTemplate;


    // TODO: 여기 인자들 DTO에 담아야하나. 너무 복잡.
    public OpenbankingDepositResponseDto depositAmount(String token, String amount, String reqClientName, String reqClientBankCode, String reqClientAccountNum) {

        String newBankTranId = bankTranId;
        newBankTranId = newBankTranId.concat(generateRandomString());
        ReqListDto reqList = ReqListDto.builder()
                .tran_no("1")
                .bank_tran_id(newBankTranId)
                .bank_code_std(bankCodeStd)
                .account_num(cntrAccountNum)
                .account_holder_name(accountHolderName)
                .print_content("입금계좌인자내역")
                .tran_amt(amount)
                .req_client_name(reqClientName)
                .req_client_bank_code(reqClientBankCode)
                .req_client_account_num(reqClientAccountNum)
                .req_client_num(generateRandomString())
                .transfer_purpose("TR")
                .build();

        List<ReqListDto> reqListDtoList = new ArrayList<>();
        reqListDtoList.add(reqList);
        OpenbankingDepositRequestDto openbankingDepositRequestDto = OpenbankingDepositRequestDto.builder()
                .cntr_account_type("N")
                .cntr_account_num(cntrAccountNum)
                .wd_pass_phrase("NONE")
                .wd_print_content("출금계좌인자내역")
                .name_check_option("off")
                .tran_dtime(getCurrentDateTime())
                .req_cnt("1")
                .req_list(reqListDtoList)
                .build();

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        // Merge
        //HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(inParams, headers);
        ResponseEntity<OpenbankingDepositRequestDto> params = new ResponseEntity<>(openbankingDepositRequestDto, headers, HttpStatus.OK);

        // POST request
        ResponseEntity<OpenbankingDepositResponseDto> response = restTemplate.exchange(
                "https://testapi.openbanking.or.kr/v2.0/transfer/deposit/acnt_num",
                HttpMethod.POST,
                params,
                OpenbankingDepositResponseDto.class
        );

        return response.getBody();
    }

    public DepositResultCheckResponseDto depositResultCheck(OpenbankingDeposit payment, String token) {
        // Parameters
        CheckReqListDto checkReqListDto = new CheckReqListDto(payment);
        DepositResultCheckDto depositResultCheckDto = new DepositResultCheckDto(payment, checkReqListDto);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        // Merge
        ResponseEntity<DepositResultCheckDto> params = new ResponseEntity<>(depositResultCheckDto, headers, HttpStatus.OK);

        // POST request
        ResponseEntity<DepositResultCheckResponseDto> response = restTemplate.exchange(
                "https://testapi.openbanking.or.kr/v2.0/transfer/result",
                HttpMethod.POST,
                params,
                DepositResultCheckResponseDto.class
        );

        return response.getBody();

    }




    public String getToken() {
        // Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", scope);
        params.add("grant_type", grantType);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Merge
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // POST request
        RestTemplate rt = new RestTemplate();
        ResponseEntity<OpenbankingTokenResponse> response = rt.exchange(
                requestUrl,
                HttpMethod.POST,
                entity,
                OpenbankingTokenResponse.class
        );

        log.info("Openbanking Token: " + response.getBody().getAccess_token());
        //log.info("왜 안되냐면: " + response.getBody());

        return response.getBody().getAccess_token();

    }

    public String generateBankTranID() {
        String newBankTranId = bankTranId;
        newBankTranId = newBankTranId.concat(generateRandomString());
        return newBankTranId;
    }

    private String generateRandomString() {
        int len = 9;
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int alphaLen = alpha.length();
        Random random = new Random();

        StringBuffer code = new StringBuffer();
        for(int i = 0; i < len; i++) {
            code.append(alpha.charAt(random.nextInt(alphaLen)));
        }

        return code.toString();
    }

    private String getCurrentDateTime() {
        //LocalDateTime now = LocalDateTime.now();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(now);
    }
}
