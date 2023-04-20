package com.pretchel.pretchel0123jwt.modules.payments.iamport.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.exception.ForgeryPaymentException;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.service.IamportMessageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/payments")
@Slf4j
public class PaymentController {
    private final IamportMessageService iamportMessageService;
    private final UserRepository userRepository;
    private final GiftRepository giftRepository;

    private final RestTemplate restTemplate;

    @Value("${iamport.key}")
    private String impKey;
    @Value("${iamport.secret}")
    private String impSecret;
    private final String impTokenReqUrl = "https://api.iamport.kr/users/getToken";
    private final String impResultCheckUrl = "https://api.iamport.kr/payments/";


    @PostMapping("/pre-create")
    public ResponseDto.Data<HashMap<String, Object>> preCreate() {
        String merchantUid = UUID.randomUUID().toString();
        HashMap<String, Object> param = new HashMap<>();
        param.put("merchant_uid", merchantUid);
        return new ResponseDto.Data<>(param);
    }

    @PostMapping("/complete")
    public ResponseDto.Empty complete(@RequestBody PaymentsCompleteDto dto) {

        checkPaymentResult(dto.getImpUid(), dto.getAmount());

        Users user = null;
        if(dto.getIsMember()) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        }

        Gift gift = giftRepository.findById(dto.getGiftId()).orElseThrow(NotFoundException::new);
        iamportMessageService.createPaymentNMessage(dto, user, gift);
        return new ResponseDto.Empty();
    }


    private void checkPaymentResult(String imp_uid, int amount) {
        String token = requestImpToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<IamportResponse> params = new ResponseEntity<>(null, headers, HttpStatus.OK);

        ResponseEntity<IamportResponse<Result>> response = restTemplate.exchange(
                impResultCheckUrl + imp_uid,
                HttpMethod.GET,
                params,
                new ParameterizedTypeReference<IamportResponse<Result>>() {}
        );

        int code = response.getStatusCodeValue();
        if(code != 200) {
            log.info(response.getBody().getMessage());
            if(code == 404)
                throw new NotFoundException("INVALID_IMP_UID");
            // 200, 404가 아닌데 실패할 일이 뭐가 있지
        }

        if(!response.getBody().getResponse().getStatus().equals("paid")) {
            // TODO: 뭐지?
        }

        if(response.getBody().getResponse().getAmount() != amount) {
            throw new ForgeryPaymentException();
        }
    }


    private String requestImpToken() {
        // Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imp_key", impKey);
        params.add("imp_secret", impSecret);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Merge
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<IamportResponse<AccessToken>> response = restTemplate.exchange(
                impTokenReqUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<IamportResponse<AccessToken>>() {}
        );
        log.info("Iamport 토큰: " + response.getBody().getMessage());

        return response.getBody().getResponse().getAccess_token();
    }

    @Getter
    public static class IamportResponse<T> {
        private int code;
        private String message;
        private T response;
    }

    @Getter
    public static class AccessToken {
        private String access_token;
        private int expired_at;
        private int now;
    }

    @Getter
    public static class Result {
        private String imp_uid;      //
        private String merchant_uid; //
        private String pay_method;
        private String channel;
        private String pg_provider;
        private String emb_pg_provider;
        private String pg_tid;
        private String pg_id;
        private Boolean escrow;
        private String apply_num;
        private String bank_code;
        private String bank_num;
        private String card_code;
        private String card_name;
        private int card_quota;
        private String card_number;
        private String card_type;
        private String vbank_code;
        private String vbank_name;
        private String vbank_num;
        private String vbank_holder;
        private int vbank_date;
        private int vbank_issued_at;
        private String name;
        private int amount; //
        private int cancel_amount; //
        private String currency;
        private String buyer_name; //
        private String buyer_email;//
        private String buyer_tel;
        private String buyer_addr;
        private String buyer_postcode;
        private String custom_data;
        private String user_agent;
        private String status;
        private int started_at;
        private int paid_at;
        private int failed_at;
        private int cancelled_at;
        private String fail_reason;
        private String cancel_reason;
        private String receipt_url;
    }

}
