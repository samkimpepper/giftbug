package com.pretchel.pretchel0123jwt.modules.gift.repository;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, String> {

    @Query("select g from Gift g where g.event = ?1")
    List<Gift> findAllByEventId(Event event);

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

    @Query("select g from Gift g limit 1000")
    List<Gift> findLimitedGifts();

}
