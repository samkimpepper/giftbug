package com.pretchel.pretchel0123jwt.v1.repository;

import com.pretchel.pretchel0123jwt.entity.Gift;
import com.pretchel.pretchel0123jwt.entity.Payments;
import com.pretchel.pretchel0123jwt.entity.PaymentsStatus;
import com.pretchel.pretchel0123jwt.v1.dto.payments.PaymentsMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentsRepository extends JpaRepository<Payments, String> {



    @Query("select p from Payments p where p.gift = ?1 and p.status = com.pretchel.pretchel0123jwt.entity.PaymentsStatus.PAID")
    List<PaymentsMapping> findAllByGiftId(Gift gift);
}
