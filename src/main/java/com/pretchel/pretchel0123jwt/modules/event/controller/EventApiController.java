package com.pretchel.pretchel0123jwt.modules.event.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.global.util.Paginator;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventDetailDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventListDto;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventApiController {

    private final EventService eventService;
    private final UserRepository userRepository;
    private static final Integer PROFILES_PER_PAGE = 12;
    private static final Integer PAGES_PER_BLOCK = 5;

    @PostMapping
    public ResponseDto.Empty save(EventCreateDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        eventService.save(dto, user);
        return new ResponseDto.Empty();
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<?> getAllEvents(@PathVariable("page") Integer page) {
        log.info("페이지:" + page);

        try {
            Paginator paginator = new Paginator(PAGES_PER_BLOCK, PROFILES_PER_PAGE, eventService.count());
            Map<String, Object> pageInfo = paginator.getFixedBlock(page);
            for(String key : pageInfo.keySet()) {
                Object val = pageInfo.get(key);
                System.out.println(key + ": " + val);
            }
        } catch (IllegalStateException ex) {
            log.info("컨트롤러 페이지네이션 실패");
        }

        return eventService.findAllByOrderByCreateDate(page, PROFILES_PER_PAGE);
    }

    @GetMapping("/my")
    public ResponseDto.DataList<EventListDto> getMyEvents() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        return new ResponseDto.DataList<>(eventService.getMyEvents(user));
    }

    @GetMapping("/{id}")
    public ResponseDto.Data<EventDetailDto> getThisEvent(@PathVariable("id") String id) {
        return new ResponseDto.Data<>(eventService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    public ResponseDto.Empty delete(@PathVariable("id") String id) {

        eventService.delete(id);
        return new ResponseDto.Empty();
    }
}
