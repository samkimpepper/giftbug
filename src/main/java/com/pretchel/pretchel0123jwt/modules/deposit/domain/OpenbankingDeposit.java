package com.pretchel.pretchel0123jwt.modules.deposit.domain;

import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.model.BaseTime;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.ResListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenbankingDeposit extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bank_tran_id;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String tran_dtime;

    @Column(nullable = false)
    private String tran_no;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpenbankingStatus status;

    private String responseMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_id")
    private Gift gift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_gift_id")
    private CompletedGift completedGift;

    public static OpenbankingDeposit of(OpenbankingDepositResponseDto dto, Gift gift, Users receiver, OpenbankingStatus status) {
        ResListDto resList = dto.getRes_list().get(0);

        String bankTranID = resList.getBank_tran_id();
        int amount = Integer.parseInt(resList.getTran_amt());
        String tranDTime = dto.getApi_tran_dtm();
        String tranNo = resList.getTran_no();

        return OpenbankingDeposit.builder()
                .bank_tran_id(bankTranID)
                .amount(amount)
                .tran_dtime(tranDTime)
                .tran_no(tranNo)
                .status(status)
                .receiver(receiver)
                .gift(gift)
                .build();
    }

    public void success() {
        status = OpenbankingStatus.PAID;
    }

    public void needToCheckLater() {
        status = OpenbankingStatus.UNCHECKED;
    }

    public void fail() {
        status = OpenbankingStatus.FAILED;
    }

    public void moveToCompletedGift(CompletedGift completedGift) {
        gift = null;
        this.completedGift = completedGift;
    }
}
