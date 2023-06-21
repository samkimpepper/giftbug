package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.EmptyValueExistsException;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.modules.info.service.AccountService;
import com.pretchel.pretchel0123jwt.modules.info.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gift")
public class GiftController {
    private final GiftService giftService;
    private final EventService eventService;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;

    @PostMapping
    public ResponseDto.Empty save(@RequestBody GiftCreateDto dto) {
        if(!StringUtils.hasText(dto.getAccountId()) || !StringUtils.hasText(dto.getAddressId())) {
            throw new EmptyValueExistsException();
        }

        Event event = eventService.findById(dto.getEventId());
        Account account = accountRepository.findById(dto.getAccountId()).orElseThrow(NotFoundException::new);
        Address address = addressRepository.findById(dto.getAddressId()).orElseThrow(NotFoundException::new);

        giftService.save(dto, event, account, address);
        return new ResponseDto.Empty();
    }

    // 강제 만료
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
