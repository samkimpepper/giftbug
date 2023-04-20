package com.pretchel.pretchel0123jwt.modules.notification;

import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import com.pretchel.pretchel0123jwt.modules.notification.dto.NotificationListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<NotificationListDto> getNotifications(Users user) {
        List<Notification> notifications = notificationRepository.findAllByListener(user);

        return notifications.stream()
                .map(notification -> {
                    return new NotificationListDto(notification.getContent(), notification.getLink());
                })
                .collect(Collectors.toList());
    }

    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @Transactional
    public void checkNotification(String id) {
        Notification notification = notificationRepository.findById(Long.parseLong(id)).orElseThrow(NotFoundException::new);
        notification.checked();
    }

    @Transactional
    public void deleteCheckedNotifications(Users user) {
        notificationRepository.deleteAllByCheckedTrueAndListener(user);

    }

}
