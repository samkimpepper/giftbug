package com.pretchel.pretchel0123jwt.modules.payments.message;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.notification.event.MessageCreatedEvent;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final GiftRepository giftRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createMessage(IamportPayment payments, Users user, Gift gift) {
        Message message = Message.builder()
                        .nickname(payments.getBuyerName())
                        .content(payments.getMessage())
                        .amount(payments.getAmount())
                        .payments(payments)
                        .gift(gift)
                        .build();

        messageRepository.save(message);

        // 알림 보냄
        eventPublisher.publishEvent(new MessageCreatedEvent(message, gift, message.getNickname(), user));
    }

}
