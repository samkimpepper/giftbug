package com.pretchel.pretchel0123jwt.modules.event.controller;

import com.pretchel.pretchel0123jwt.infra.global.Response;
import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.dto.gift.GiftCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import com.pretchel.pretchel0123jwt.modules.event.service.GiftService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.service.AccountService;
import com.pretchel.pretchel0123jwt.modules.info.service.AddressService;
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
    private final AccountService accountService;
    private final AddressService addressService;
    private final Response responseDto;


    @PostMapping
    public ResponseDto.Empty save(@RequestBody GiftCreateDto dto) {

        Event event = eventService.findById(dto.getEventId());
        Account account = accountService.findById(dto.getAccountId());
        Address address = addressService.findById(dto.getAddressId());

        giftService.save(dto, event, account, address);
        return new ResponseDto.Empty();
    }

    @PutMapping("/complete/{id}")
    public ResponseDto.Empty complete(@PathVariable("id") String id) {
        giftService.complete(id);
        return new ResponseDto.Empty();
    }

    @PutMapping("/finish/{id}")
    public ResponseDto.Empty finish(@PathVariable("id") String id) {
        giftService.finish(id);
        return new ResponseDto.Empty();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getGiftDetail(@PathVariable("id") String giftId) {
//
//    }

}
