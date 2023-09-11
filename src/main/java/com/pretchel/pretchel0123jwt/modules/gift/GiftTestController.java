package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/gift")
public class GiftTestController {
    private final EventService eventService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final GiftTestService giftTestService;

    @PostMapping
    public ResponseDto.Empty create(@RequestParam String eventId) {
        Event event = eventService.findById(eventId);
        Users user = event.getUsers();
        Account account = accountRepository.findAllByUsers(user).get(0);
        Address address = addressRepository.findAllByUsers(user).get(0);
        giftTestService.create(event, account, address);
        return new ResponseDto.Empty();
    }

    @PostMapping("/all")
    public ResponseDto.Empty createAllGiftsPerEvent() {
       giftTestService.createAllGifts();
       return new ResponseDto.Empty();
    }
}
