package com.pretchel.pretchel0123jwt.modules.payments.iamport.repository;

import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IamportPaymentRepository extends JpaRepository<IamportPayment, String> {

    List<IamportPayment> findAllByGift(Gift gift);

    List<IamportPayment> findAllByCompletedGift(CompletedGift completedGift);
}
