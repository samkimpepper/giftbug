package com.pretchel.pretchel0123jwt.v1.event.controller;

import com.pretchel.pretchel0123jwt.v1.event.dto.gift.GiftRequestDto;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.v1.event.service.EventService;
import com.pretchel.pretchel0123jwt.v1.event.service.GiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gift")
public class GiftApiController {
    private final GiftService giftService;
    private final EventService eventService;
    private final Response responseDto;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody GiftRequestDto.Save save,
                                  HttpServletRequest request) {
        Enumeration params = request.getParameterNames();
        System.out.println("--------------------------------");
        while(params.hasMoreElements()) {
            String name = (String)params.nextElement();
            System.out.println(name + " : " +request.getParameter(name));
        }

        return giftService.save(save);
    }

    @PutMapping("/finish/{id}")
    public ResponseEntity<?> complete(@PathVariable("id") String id) {
        return responseDto.success("아직 안함 힛");
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getGiftDetail(@PathVariable("id") String giftId) {
//
//    }

    @GetMapping("/event/{id}")
    public ResponseEntity<?> getMyGifts(@PathVariable("id") String id) {
        return giftService.getMyGifts(id);
    }
}
