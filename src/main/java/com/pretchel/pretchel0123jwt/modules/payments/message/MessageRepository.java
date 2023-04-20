package com.pretchel.pretchel0123jwt.modules.payments.message;

import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findAllByGift(Gift gift);
}
