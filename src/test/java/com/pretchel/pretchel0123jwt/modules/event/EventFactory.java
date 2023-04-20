package com.pretchel.pretchel0123jwt.modules.event;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class EventFactory {
    @Autowired
    EventRepository eventRepository;

    public Event createEvent(Users user, String nickname, String deadLine) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(sdf.parse(deadLine).getTime());

        Event event = Event.builder()
                .users(user)
                .nickname(nickname)
                .eventType("생일")
                .deadLine(date)
                .isExpired(false)
                .build();

        eventRepository.save(event);

        return event;
    }
}
