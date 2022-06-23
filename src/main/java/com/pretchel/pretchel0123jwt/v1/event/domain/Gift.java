package com.pretchel.pretchel0123jwt.v1.event.domain;

import com.pretchel.pretchel0123jwt.global.BaseTime;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/*
* 선물이름
* 프로필 외래키
* 가격
* 퍼센티지(이름 이걸로 해야되나. 프로그레스로 할까?
* 사진
* 링크?
*
* */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Gift extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column
    private String name;

    @Column(nullable = false)
    private int price;

    @Column
    private int remainder;

    @Column
    private String giftImageUrl;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private String story;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="address_id")
    private Address address;

    @Column
    @Enumerated(EnumType.STRING)
    private GiftState state;
}
