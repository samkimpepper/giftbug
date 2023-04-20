package com.pretchel.pretchel0123jwt.modules.payments.message;

import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Message extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payments_id", nullable = false)
    private IamportPayment payments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gift_id")
    private Gift gift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="completed_gift_id")
    private CompletedGift completedGift;

    public void moveToCompletedGift(CompletedGift completedGift) {
        gift = null;
        this.completedGift = completedGift;
    }
}
