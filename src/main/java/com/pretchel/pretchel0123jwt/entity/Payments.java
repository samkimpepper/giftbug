package com.pretchel.pretchel0123jwt.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payments {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String merchant_uid;

    @Column
    private String imp_uid;

    @Column
    private int amount;

    @Column
    private String buyerName;

    @Column
    private String message;

    @Column
    private Boolean isMember;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentsStatus status = PaymentsStatus.READY;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private LocalDateTime faildAt;

    private LocalDateTime cancelledAt;

    // nullable true
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gift_id")
    private Gift gift;

}
