package com.pretchel.pretchel0123jwt.modules.payments.message;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageTestController {
    private final MessageService messageService;

    private final GiftRepository giftRepository;

    private final IamportPaymentRepository iamportPaymentRepository;

    // 원래 이렇게 하는 거 아님.
    // 오픈뱅킹 로직 테스트 위해 만들었음
    @PostMapping("/test/message")
    public ResponseDto.Empty createMessage(@RequestBody MessageTestCreateDto dto) {
        Gift gift = giftRepository.findById(dto.getGiftId()).orElseThrow(NotFoundException::new);

        // 가짜 IamportPayment 만들어야됨
        IamportPayment mockPayment = IamportPayment.builder()
                .merchant_uid(UUID.randomUUID().toString())
                .imp_uid("test")
                .amount(dto.getPaidAmount())
                .buyerName(dto.getNickname())
                .message(dto.getContent())
                .gift(gift)
                .build();
        
        iamportPaymentRepository.save(mockPayment);

        messageService.createMessage(mockPayment);
        return new ResponseDto.Empty();
    }
}
