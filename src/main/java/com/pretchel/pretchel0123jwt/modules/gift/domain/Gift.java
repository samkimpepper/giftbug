package com.pretchel.pretchel0123jwt.modules.gift.domain;

import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

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
@Getter
@Entity
public class Gift extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Version
    private Long version;

    @Column
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private Date deadLine;

    @Column
    private int funded;

    @Column
    private String giftImageUrl;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private String story;

    @Column
    @Builder.Default
    private Integer wishes = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="address_id", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    private GiftState state;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ProcessState processState = ProcessState.none;


    public void changeState(GiftState state) {
        this.state = state;
    }

    public void changeProcessState(ProcessState state) { this.processState = state; }

    public void completeProcess() {
        processState = ProcessState.completed;
    }

    public void shouldCheckProcess() {
        processState = ProcessState.check;
    }

    public void shouldReDeposit() {
        processState = ProcessState.none;
    }

    public void pay(int paid) {
        //if(funded + paid < price) TODO: 초과하면 어떻게 되는지 상의 안 했음
        funded += paid;
    }

    public boolean isGranterPrice() {
        return (funded >= price);
    }

    public void increaseWishesCount() {
        this.wishes++;
    }

    public void decreaseWishesCount() {
        this.wishes--;
    }
}
