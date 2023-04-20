package com.pretchel.pretchel0123jwt.modules.info.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountListDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressListDto;
import com.pretchel.pretchel0123jwt.modules.info.service.AccountService;
import com.pretchel.pretchel0123jwt.modules.info.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressAccountController {
    private final AddressService addressService;
    private final AccountService accountService;

    @PostMapping("/address")
    public ResponseDto.Empty createAddress(@RequestBody AddressCreateDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        addressService.createAddress(dto, email);
        return new ResponseDto.Empty();
    }

    @PostMapping("/account")
    public ResponseDto.Empty createAccount(@RequestBody AccountCreateDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        accountService.createAccount(dto, email);
        return new ResponseDto.Empty();
    }

    @GetMapping("/address")
    public ResponseDto.DataList<AddressListDto> getAllMyAddresses() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto.DataList<>(addressService.getAllMyAddresses(email));
    }

    @GetMapping("/account")
    public ResponseDto.DataList<AccountListDto> getAllMyAccounts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto.DataList<>(accountService.getAllMyAccounts(email));
    }

    @DeleteMapping("/address/{id}")
    public ResponseDto.Empty deleteAddress(@PathVariable("id") String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        addressService.delete(email, id);
        return new ResponseDto.Empty();
    }
}
