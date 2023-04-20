package com.pretchel.pretchel0123jwt.modules.notification.event;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationEvent;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;

public class PaymentsCompletedEvent implements NotificationEvent {
    private final Users listener;
    private final String content;
    private final String link;

    public PaymentsCompletedEvent(Users listener, IamportPayment payments) {
        this.listener = listener;
        this.content = payments.getGift().getName() + "에 보내는 " + payments.getAmount() + "원 결제가 완료되었습니다.";
        this.link = "/api";
    }

    public Notification createNotification() {
        return Notification.builder()
                .checked(false)
                .link(link)
                .content(content)
                .listener(listener)
                .notificationType(NotificationType.PAYMENTS_COMPLETED)
                .build();
    }
}
