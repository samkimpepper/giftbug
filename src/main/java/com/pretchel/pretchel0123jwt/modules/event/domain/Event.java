package com.pretchel.pretchel0123jwt.modules.event.domain;

import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Event extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users users;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String eventType;

    @Column
    private String profileImageUrl;

    @Column
    private String backgroundImageUrl;

    @Column
    @Temporal(TemporalType.DATE)
    private Date deadLine;

    @Column
    private Boolean isExpired;

}
