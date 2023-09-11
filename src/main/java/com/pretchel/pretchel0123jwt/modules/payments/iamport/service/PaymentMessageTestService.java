package com.pretchel.pretchel0123jwt.modules.payments.iamport.service;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.notification.event.MessageCreatedEvent;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.PaymentsStatus;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMessageTestService {
    private final GiftRepository giftRepository;

    private final GiftService giftService;
    private final IamportPaymentRepository paymentRepository;
    private final MessageRepository messageRepository;

    private final ApplicationEventPublisher eventPublisher;

    private int[] prices = {5000, 10000, 15000, 20000, 25000, 30000};

    @Transactional
    public void createPaymentAndMessagePerAllGifts() {
        Random random = new Random();
        List<Gift> gifts = giftRepository.findAll();
        for(Gift gift : gifts) {
            int createCount = random.nextInt(10);
            for(int i=0; i<createCount; i++) {
                createPaymentAndMessage(gift);
            }
        }
    }

    private void createPaymentAndMessage(Gift gift) {
        IamportPayment payment = createPayment(gift);
        createMessage(gift, payment);
        giftService.fund(gift, payment.getAmount());
    }

    private IamportPayment createPayment(Gift gift) {
        Random random = new Random();
        IamportPayment payment = IamportPayment.builder()
                .merchant_uid(UUID.randomUUID().toString())
                .imp_uid(UUID.randomUUID().toString())
                .amount(prices[random.nextInt(prices.length)])
                .status(PaymentsStatus.PAID)
                .gift(gift)
                .build();
        paymentRepository.save(payment);
        return payment;
    }

    private void createMessage(Gift gift, IamportPayment payment) {
        Message message = Message.builder()
                .nickname("호냐냐")
                .content("축하해!")
                .amount(payment.getAmount())
                .payments(payment)
                .gift(gift)
                .build();
        messageRepository.save(message);
        Users receiver = gift.getEvent().getUsers();
        eventPublisher.publishEvent(new MessageCreatedEvent(message, gift, message.getNickname(), receiver));

    }

}
