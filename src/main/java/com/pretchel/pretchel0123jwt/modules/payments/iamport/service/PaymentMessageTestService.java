package com.pretchel.pretchel0123jwt.modules.payments.iamport.service;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.notification.event.MessageCreatedEvent;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.PaymentsStatus;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.PaymentJdbcRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageJdbcRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PaymentMessageTestService {
    private final PaymentJdbcRepository paymentJdbcRepository;

    private final MessageJdbcRepository messageJdbcRepository;
    private final GiftRepository giftRepository;

    private final GiftService giftService;
    private final IamportPaymentRepository paymentRepository;
    private final MessageRepository messageRepository;

    private final ApplicationEventPublisher eventPublisher;

    private int[] prices = {5000, 10000, 15000, 20000, 25000, 30000};

    public void batchUpdatePaymentAndMessagePerAllGifts() {
        Random random = new Random();
        List<Gift> gifts = giftRepository.findAll();
        List<IamportPayment> payments = gifts.stream()
                        .flatMap(gift -> {
                            int createCount = random.nextInt(10);
                            return Stream.generate(() -> generatePayment(gift)).limit(createCount);
                        })
                        .collect(Collectors.toList());
        paymentJdbcRepository.batchUpdatePayments(payments);

        List<Message> messages = payments.stream()
                        .map(payment -> {
                            Gift gift = payment.getGift();
                            giftService.fund(gift, payment.getAmount());
                            return generateMessage(gift, payment);
                        })
                        .collect(Collectors.toList());
        messageJdbcRepository.batchUpdateMessages(messages);
    }

//    public void batchUpdatePaymentAndMessagePerAllGifts() {
//        Random random = new Random();
//        List<Gift> gifts = giftRepository.findAll();
//        List<IamportPayment> payments = new ArrayList<>();
//
//        for(Gift gift : gifts) {
//            int createCount = random.nextInt(10);
//            for(int i=0; i<createCount; i++) {
//                payments.add(generatePayment(gift));
//            }
//        }
//        paymentJdbcRepository.batchUpdatePayments(payments);
//
//        List<Message> messages = new ArrayList<>();
//        for(IamportPayment payment : payments) {
//            Gift gift = payment.getGift();
//            messages.add(generateMessage(gift, payment));
//            giftService.fund(gift, payment.getAmount());
//        }
//        messageJdbcRepository.batchUpdateMessages(messages);
//    }

    private IamportPayment generatePayment(Gift gift) {
        Random random = new Random();
        IamportPayment payment = IamportPayment.builder()
                .merchant_uid(UUID.randomUUID().toString())
                .imp_uid(UUID.randomUUID().toString())
                .amount(prices[random.nextInt(prices.length)])
                .status(PaymentsStatus.PAID)
                .gift(gift)
                .build();
        return payment;
    }

    private Message generateMessage(Gift gift, IamportPayment payment) {
        Message message = Message.builder()
                .nickname("호냐냐")
                .content("축하해!")
                .amount(payment.getAmount())
                .payments(payment)
                .gift(gift)
                .build();
        //messageRepository.save(message);
        Users receiver = gift.getEvent().getUsers();

        eventPublisher.publishEvent(new MessageCreatedEvent(message, gift, message.getNickname(), receiver));

        return message;
    }

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
