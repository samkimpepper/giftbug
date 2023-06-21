package com.pretchel.pretchel0123jwt.modules.payments.iamport.domain;

import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IamportPayment extends BaseTime {
    @Id
    @Column(length = 36)
    private String merchant_uid;

    @Column
    private String imp_uid;

    @Column
    private int amount;

    @Column
    private String buyerName;

    @Column
    private String buyerEmail;

    @Column
    private String message;

    @Column
    private Boolean isMember;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentsStatus status = PaymentsStatus.READY;

    private LocalDateTime paidAt;

    private LocalDateTime faildAt;

    private LocalDateTime cancelledAt;

    // null이면 비회원이라는 뜻?
    // 비회원 결제도 가능해서 nullable=false일 필요 없음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_id")
    private Gift gift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_gift_id")
    private CompletedGift completedGift;


    public static IamportPayment complete(PaymentsCompleteDto dto, Users user, Gift gift) {
        return IamportPayment.builder()
                .merchant_uid(dto.getMerchantUid())
                .imp_uid(dto.getImpUid())
                .amount(dto.getAmount())
                .status(PaymentsStatus.PAID)
                .buyerName(dto.getBuyerName())
                .buyerEmail(dto.getBuyerEmail())
                .message(dto.getMessage())
                .isMember(dto.getIsMember())
                .gift(gift)
                .buyer(user)
                .build();

    }


    public void moveToCompletedGift(CompletedGift completedGift) {
        gift = null;
        this.completedGift = completedGift;
    }
}