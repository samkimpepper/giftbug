package com.pretchel.pretchel0123jwt.modules.gift.repository;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface GiftRepository extends JpaRepository<Gift, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Gift g where g.id = ?1")
    Optional<Gift> findByIdWithPessimisticLock(String id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select g from Gift g where g.id = ?1")
    Optional<Gift> findByIdWithOptimisticLock(String id);

    @Query("select g from Gift g where g.event = ?1")
    List<Gift> findAllByEventId(@Param("event") Event event);

    List<Gift> findAllByEvent(Event event);

    @Query("select g from Gift g where g.state in (:giftState)")
    List<Gift> findAllByState(@Param("giftState") GiftState giftState);

    @Query("select g from Gift g where g.state in (:giftState) and g.processState in (:processState)")
    List<Gift> findAllByStateInAndProcessStateIn(@Param("giftState") GiftState giftState, @Param("processState") ProcessState processState);

    @Query("select g from Gift g where g.state not in (:giftState) and g.processState in (:processState)")
    List<Gift> findAllByStateNotInAndProcessStateIn(@Param("giftState") GiftState giftState, @Param("processState") ProcessState processState);

    Boolean existsByAddress(Address address);

    void deleteAllByEvent(Event event);

    List<Gift> findAllByOrderByWishesDesc();

    List<Gift> findTop1000ByOrderByIdDesc();

}
