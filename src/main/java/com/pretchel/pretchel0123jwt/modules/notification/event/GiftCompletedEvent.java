package com.pretchel.pretchel0123jwt.modules.notification.event;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationEvent;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;
import lombok.Getter;

@Getter
public class GiftCompletedEvent implements NotificationEvent {
    private final Users listener;
    private final String link;
    private final String content;

    public GiftCompletedEvent(Gift gift, Users listener) {
        this.listener = listener;
        this.link = "/api/gift" + gift.getId();
        this.content = "선물 " + gift.getName() + "의 펀딩이 완료되었습니다.";
    }

    public Notification createNotification() {
        return Notification.builder()
                .checked(false)
                .link(link)
                .content(content)
                .listener(listener)
                .notificationType(NotificationType.GIFT_COMPLETED)
                .build();
    }
}
