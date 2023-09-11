package com.pretchel.pretchel0123jwt.modules.event.controller;

import com.mysql.cj.jdbc.MysqlXAConnection;
import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.global.util.Paginator;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventDetailDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventListDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventPagingDto;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.MariaDB10Dialect;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
//@Api(value = "이벤트 컨트롤러")
public class EventApiController {

    private final EventService eventService;

    private final GiftService giftService;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private static final Integer EVENTS_PER_PAGE = 12;
    private static final Integer PAGES_PER_BLOCK = 5;

    @PostMapping
    public ResponseDto.Empty save(EventCreateDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        eventService.save(dto, user);

        return new ResponseDto.Empty();
    }

    @GetMapping("/page/{page}")
    //@ApiOperation("모든 이벤트 조회")
    public ResponseDto.Data<EventPagingDto> getAllEvents(@PathVariable("page") Integer page) {
        Paginator paginator = new Paginator(PAGES_PER_BLOCK, EVENTS_PER_PAGE, eventService.count());
        Map<String, Object> pageInfo = paginator.getFixedBlock(page);

        List<EventListDto> eventList = eventService.findAllByOrderByCreateDate(page, EVENTS_PER_PAGE);

        EventPagingDto eventPagingDto = EventPagingDto.builder()
                .pageInfo(pageInfo)
                .eventList(eventList)
                .build();
        return new ResponseDto.Data<>(eventPagingDto);
    }

    @GetMapping("/{id}")
    //@ApiOperation(value = "이벤트 상세 조회")
    public ResponseDto.Data<EventDetailDto> getEvent(@PathVariable("id") String id) {
        return new ResponseDto.Data<>(eventService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    //@ApiOperation(value = "이벤트 삭제")
    public ResponseDto.Empty delete(@PathVariable("id") String id) {
        Event event = eventRepository.findById(id).orElseThrow(NotFoundException::new);
        giftService.deleteAllByEvent(event);
        eventService.delete(id);
        return new ResponseDto.Empty();
    }
}
