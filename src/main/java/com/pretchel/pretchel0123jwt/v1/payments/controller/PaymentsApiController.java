//package com.pretchel.pretchel0123jwt.v1.payments.controller;
//
//import com.pretchel.pretchel0123jwt.v1.payments.dto.PaymentsRequestDto;
//import com.pretchel.pretchel0123jwt.global.Response;
//import com.pretchel.pretchel0123jwt.v1.payments.service.PaymentsService;
//import com.siot.IamportRestClient.IamportClient;
//import com.siot.IamportRestClient.exception.IamportResponseException;
//import com.siot.IamportRestClient.response.IamportResponse;
//import com.siot.IamportRestClient.response.Payment;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.json.simple.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//
//import javax.validation.Valid;
//import java.io.IOException;
//
///*
//* 결제 성공 후
//* 토큰을 발급받음(getToken)
//* 토큰 헤더에 추가해서
//* */
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//@RequestMapping("/api/payments")
//public class PaymentsApiController {
//    private final IamportClient api;
//    private final PaymentsService paymentsService;
//    private final Response responseDto;
//
//
//    @Value("${import.key}")
//    private String key;
//
//    @Value("${import.secret}")
//    private String secret;
//
//    @PostMapping
//    public ResponseEntity<?> save(@Valid @RequestBody PaymentsRequestDto.Save save) {
//        return paymentsService.save(save);
//    }
//
//
//    @PostMapping("/complete")
//    public ResponseEntity<?> complete(@RequestBody PaymentsRequestDto.Complete complete) throws IamportResponseException, IOException {
//        String imp_uid = complete.getImpUid();
//        String merchant_uid = complete.getMerchantUid();
//        int paid_amount = complete.getPaidAmount();
//        log.info("여기는 Payments 컨트롤러다 오바.. ");
//
//        getToken();
//
//        IamportResponse<Payment> paymentData = api.paymentByImpUid(imp_uid);
//        if(paid_amount == paymentData.getResponse().getAmount().intValue()) {
//            // 결제 성공
//            return paymentsService.complete(complete);
//        } else {
//
//            return paymentsService.fail(complete);
//        }
//    }
//
//    private void getToken() {
//        HttpHeaders headers = new HttpHeaders();
//        //headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
//
//        String url = "https://api.iamport.kr/users/getToken";
//
//        JSONObject request = new JSONObject();
//
//        request.put("imp_key", key);
//        request.put("imp_secret", secret);
//
//        log.info(request.get("imp_key").toString());
//
//        HttpEntity<JSONObject> entity = new HttpEntity<>(request, headers);
//        ResponseEntity<JSONObject> token = restTemplate.postForEntity(url, entity, JSONObject.class);
//
//        //MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        //map.add("", "");
//
//        System.out.println("************************");
//        System.out.println(token);
//    }
//}
