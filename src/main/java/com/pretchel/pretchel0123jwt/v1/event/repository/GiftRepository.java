package com.pretchel.pretchel0123jwt.v1.event.repository;

import com.pretchel.pretchel0123jwt.v1.event.domain.Event;
import com.pretchel.pretchel0123jwt.v1.event.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, String> {

    @Query("select g from Gift g where g.event = ?1")
    List<Gift> findAllByEventId(Event event);
}
