//package com.pretchel.pretchel0123jwt.v1.payments.repository;
//
//import com.pretchel.pretchel0123jwt.v1.event.domain.Gift;
//import com.pretchel.pretchel0123jwt.v1.payments.domain.Payments;
//import com.pretchel.pretchel0123jwt.v1.payments.dto.PaymentsMapping;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.util.List;
//
//public interface PaymentsRepository extends JpaRepository<Payments, String> {
//
//
//
//    @Query("select p from Payments p where p.gift = ?1 and p.status = com.pretchel.pretchel0123jwt.entity.PaymentsStatus.PAID")
//    List<PaymentsMapping> findAllByGiftId(Gift gift);
//}
