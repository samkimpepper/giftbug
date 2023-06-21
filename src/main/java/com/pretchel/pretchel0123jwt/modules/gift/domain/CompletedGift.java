package com.pretchel.pretchel0123jwt.modules.gift.domain;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/*
* completed, expired, finished 선물 처리 완료되면 이곳으로 옮김
* */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class CompletedGift extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private int price;

    @Column
    private int funded;

    @Column
    private Date deadLine;

    @Enumerated(EnumType.STRING)
    private GiftState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users user;

    public static CompletedGift fromGift(Gift gift) {
        return CompletedGift.builder()
                .name(gift.getName())
                .price(gift.getPrice())
                .funded(gift.getFunded())
                .deadLine(gift.getDeadLine())
                .state(gift.getState())
                .user(gift.getEvent().getUsers())
                .build();
    }

}
