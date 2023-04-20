package com.pretchel.pretchel0123jwt.modules.notification.dto;

import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class NotificationListDto {
    private String content;
    private String link;

}
