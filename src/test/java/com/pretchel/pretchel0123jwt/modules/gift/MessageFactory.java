package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {
    @Autowired
    private MessageRepository messageRepository;

    public Message createMessage(Payments payments, Gift gift) {
        Message message = Message.builder()
                .nickname(payments.getBuyerName())
                .content(payments.getMessage())
                .amount(payments.getAmount())
                .payments(payments)
                .gift(gift)
                .build();

        messageRepository.save(message);
        return message;
    }
}
