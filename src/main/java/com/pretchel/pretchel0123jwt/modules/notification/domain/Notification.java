package com.pretchel.pretchel0123jwt.modules.notification.domain;

import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseTime {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String content;

    @Column
    private String link;

    @Column
    private boolean checked;

    // TODO: 무조건 펀딩한 사람 익명으로 할건지 상의
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Users publisher;

    @Column
    private String publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users listener;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    public void checked() {
        checked = true;
    }



}
