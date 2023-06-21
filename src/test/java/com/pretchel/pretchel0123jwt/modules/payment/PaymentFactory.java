package com.pretchel.pretchel0123jwt.modules.payment;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.service.IamportMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentFactory {
    @Autowired
    private IamportMessageService iamportMessageService;

    public void createPayment(int amount, Gift gift, Users buyer) {
        iamportMessageService.createPaymentNMessage(generatePaymentsCompleteDto(amount, gift, buyer), buyer, gift);
    }

    private PaymentsCompleteDto generatePaymentsCompleteDto(int amount, Gift gift, Users buyer) {
        return PaymentsCompleteDto.builder()
                .merchantUid("d2dkv1jnvkctt4z")
                .impUid("머라카지")
                .amount(amount)
                .buyerName("이닭")
                .buyerEmail(buyer.getEmail())
                .isMember(true)
                .message("생일 축하해!")
                .giftId(gift.getId())
                .build();
    }
}
