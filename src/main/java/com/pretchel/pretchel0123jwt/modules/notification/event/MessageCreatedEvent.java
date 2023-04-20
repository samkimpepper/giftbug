package com.pretchel.pretchel0123jwt.modules.notification.event;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationEvent;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;
import lombok.Getter;

@Getter
public class MessageCreatedEvent implements NotificationEvent {
    private final Message message;
    private final Gift gift;
    private final String publisher;
    private final Users listener;
    private final String link;
    private final String content;

    public MessageCreatedEvent(Message message, Gift gift, String publisher,  Users listener) {
        this.message = message;
        this.gift = gift;
        this.publisher = publisher;
        this.listener = listener;
        this.link = "/api/gift/" + gift.getId();
        this.content = publisher + "님께서 " + gift.getName() + "에 " + message.getAmount() + "원의 메세지를 남기셨습니다.";
    }

    public Notification createNotification() {
        return Notification.builder()
                .checked(false)
                .link(link)
                .content(content)
                .listener(listener)
                .publisher(publisher)
                .notificationType(NotificationType.MESSAGE_RECEIVED)
                .build();
    }
}
