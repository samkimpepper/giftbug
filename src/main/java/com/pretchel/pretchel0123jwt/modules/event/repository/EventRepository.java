package com.pretchel.pretchel0123jwt.modules.event.repository;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findAllByUsers(Users users);

    Page<Event> findAll(Pageable pageable);
}
