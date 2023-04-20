package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.response.UserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyPasswordDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserSettingController {
    private final UserSettingService userSettingService;

    @PutMapping("/update")
    public ResponseDto.Empty update(@RequestBody ModifyInfoDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userSettingService.update(dto, email);
        return new ResponseDto.Empty();
    }

    @GetMapping("/user-info")
    public ResponseDto.Data<UserInfoDto> userInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto.Data<>(userSettingService.getUserInfo(email));
    }



    @PutMapping("/update-password")
    public ResponseDto.Empty updatePassword(@RequestBody ModifyPasswordDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userSettingService.updatePassword(dto, email);
        return new ResponseDto.Empty();
    }
}
