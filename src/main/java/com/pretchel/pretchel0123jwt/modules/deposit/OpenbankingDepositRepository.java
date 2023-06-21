package com.pretchel.pretchel0123jwt.modules.deposit;

import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenbankingDepositRepository extends JpaRepository<OpenbankingDeposit, Long> {
    List<OpenbankingDeposit> findAllByGift(Gift gift, Sort sort);

    List<OpenbankingDeposit> findAllByGift(Gift gift);

    List<OpenbankingDeposit> findAllByCompletedGift(CompletedGift completedGift);


    List<OpenbankingDeposit> findAllByStatus(OpenbankingStatus status);
}
