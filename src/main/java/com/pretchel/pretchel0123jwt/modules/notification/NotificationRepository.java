package com.pretchel.pretchel0123jwt.modules.notification;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByListener(Users listener);

    void deleteAllByCheckedTrueAndListener(Users listener);

}
