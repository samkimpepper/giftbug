package com.pretchel.pretchel0123jwt.modules.notification;

import com.pretchel.pretchel0123jwt.modules.notification.NotificationService;
import com.pretchel.pretchel0123jwt.modules.notification.domain.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationService notificationService;


    @EventListener
    public void saveNotification(NotificationEvent event) {

        notificationService.createNotification(event.createNotification());
    }


}
