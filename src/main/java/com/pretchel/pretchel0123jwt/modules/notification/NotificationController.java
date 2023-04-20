package com.pretchel.pretchel0123jwt.modules.notification;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.notification.dto.NotificationListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping("/notifications")
    public ResponseDto.DataList<NotificationListDto> getNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        return new ResponseDto.DataList<>(notificationService.getNotifications(user));
    }

    @PutMapping("/notification/check/{id}")
    public ResponseDto.Empty checkNotification(@PathVariable("id") String id) {
        notificationService.checkNotification(id);
        return new ResponseDto.Empty();
    }

    @DeleteMapping("/notifications/checked")
    public ResponseDto.DataList<NotificationListDto> deleteCheckedNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        notificationService.deleteCheckedNotifications(user);
        return new ResponseDto.DataList<>(notificationService.getNotifications(user));
    }
}
