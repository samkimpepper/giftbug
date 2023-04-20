package com.pretchel.pretchel0123jwt.modules.notification.event;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationEvent;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;

public class ExpiredGiftDepositEvent implements NotificationEvent {
    private final Users listener;
    private final String content;
    private final String link;

    public ExpiredGiftDepositEvent(Users listener, OpenbankingDeposit deposit) {
        this.listener = listener;
        this.content = "만료된 선물 " + deposit.getGift().getName() + "의 대금 " + deposit.getAmount() +"원이 이체되었습니다.";
        this.link = "/api/"; // 결제 내역 모아두는 api 안 만들었음
    }

    public Notification createNotification() {
        return Notification.builder()
                .checked(false)
                .link(link)
                .content(content)
                .listener(listener)
                .notificationType(NotificationType.EXPIRED_GIFT_DEPOSIT)
                .build();
    }
}
