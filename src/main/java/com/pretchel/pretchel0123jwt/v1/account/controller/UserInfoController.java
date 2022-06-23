package com.pretchel.pretchel0123jwt.v1.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.v1.account.dto.user.UserEventsDto;
import com.pretchel.pretchel0123jwt.v1.account.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserInfoController {
    private final UsersService usersService;

    @GetMapping("/user-info")
    public ResponseDto.DataList<UserEventsDto> userInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseDto.DataList<>(usersService.getUserEvents(userDetails));
    }


}
