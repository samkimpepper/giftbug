package com.pretchel.pretchel0123jwt.v1.repository;

import com.pretchel.pretchel0123jwt.entity.Event;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.dto.profile.EventMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, String> {

    @Query("select p from Event p where p.users = ?1")
    List<EventMapping> findProfilesByUserId(Users users);

    Page<Event> findAll(Pageable pageable);
}
