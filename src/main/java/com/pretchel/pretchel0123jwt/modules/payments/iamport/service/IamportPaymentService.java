package com.pretchel.pretchel0123jwt.modules.payments.iamport.service;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.notification.event.PaymentsCompletedEvent;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IamportPaymentService {
    private final IamportPaymentRepository iamportPaymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public IamportPayment createPayment(PaymentsCompleteDto dto, Users user, Gift gift) {
        IamportPayment payment = IamportPayment.complete(dto, user, gift);
        iamportPaymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentsCompletedEvent(user, payment));

        return payment;
    }


}
