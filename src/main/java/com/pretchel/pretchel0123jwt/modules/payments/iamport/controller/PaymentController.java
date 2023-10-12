package com.pretchel.pretchel0123jwt.modules.payments.iamport.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.IamportPaymentApi;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.service.IamportMessageService;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.service.PaymentMessageTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final IamportPaymentApi iamportPaymentApi;

    private final PaymentMessageTestService paymentMessageTestService;

    @PostMapping("/pre-create")
    public ResponseDto.Data<HashMap<String, Object>> preCreate() {
        String merchantUid = UUID.randomUUID().toString();
        HashMap<String, Object> param = new HashMap<>();
        param.put("merchant_uid", merchantUid);
        return new ResponseDto.Data<>(param);
    }

    @PostMapping("/complete")
    public ResponseDto.Empty complete(@RequestBody PaymentsCompleteDto dto) {

        iamportPaymentApi.checkPaymentResult(dto.getImpUid(), dto.getAmount());

        Users user = null;
        if(dto.getIsMember()) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        }

        Gift gift = giftRepository.findById(dto.getGiftId()).orElseThrow(NotFoundException::new);
        iamportMessageService.createPaymentNMessage(dto, user, gift);
        return new ResponseDto.Empty();
    }

    @PostMapping("/complete/test")
    public ResponseDto.Empty testComplete(@RequestBody PaymentsCompleteDto dto) {
        String merchantUid = UUID.randomUUID().toString();
        String impUid = UUID.randomUUID().toString();
        dto.setMerchantUid(merchantUid);
        dto.setImpUid(impUid);

        Users user = null;
        if(dto.getIsMember()) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        }

        Gift gift = giftRepository.findById(dto.getGiftId()).orElseThrow(NotFoundException::new);
        iamportMessageService.createPaymentNMessage(dto, user, gift);
        return new ResponseDto.Empty();
    }

    @PostMapping("/all")
    public ResponseDto.Empty createPaymentsPerAllGift() {
        paymentMessageTestService.createPaymentAndMessagePerAllGifts();
        return new ResponseDto.Empty();
    }
}
