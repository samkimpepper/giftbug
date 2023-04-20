package com.pretchel.pretchel0123jwt.modules.notification.event;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationEvent;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationType;

import java.time.LocalDateTime;

public class PasswordChangedEvent implements NotificationEvent {
    private final Users listener;
    private final String link;
    private final String content;

    public PasswordChangedEvent(Users listener, LocalDateTime modifiedAt) {
        this.listener = listener;
        this.link = "/api/"; // TODO: 모르겠다
        this.content = modifiedAt + "에 비밀번호가 변경되었습니다.";
    }

    public Notification createNotification() {
        return Notification.builder()
                .checked(false)
                .link(link)
                .content(content)
                .listener(listener)
                .notificationType(NotificationType.PASSWORD_CHANGED)
                .build();
    }
}
