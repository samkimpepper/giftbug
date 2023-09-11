package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.response.UserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UpdateUserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UpdatePasswordDto;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserSettingService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
//@Api(value="사용자 정보 변경 컨트롤러")
public class UserSettingController {
    private final UserSettingService userSettingService;

    private final AccountService accountService;

    private final UserRepository userRepository;

    @PutMapping("/update")
    //@ApiOperation(value = "내 정보 변경", notes = "생일과 전화번호 변경 가능")
    public ResponseDto.Empty update(@RequestBody UpdateUserInfoDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userSettingService.update(dto, email);
        return new ResponseDto.Empty();
    }

    @GetMapping("/user-info")
    //@ApiOperation(value = "내 정보 조회")
    public ResponseDto.Data<UserInfoDto> userInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        Account account = accountService.findDefaultAccountByUser(user);
        return new ResponseDto.Data<>(userSettingService.getUserInfo(user, account));
    }



    @PutMapping("/update-password")
    //@ApiOperation(value = "비밀번호 변경")
    public ResponseDto.Empty updatePassword(@RequestBody UpdatePasswordDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userSettingService.updatePassword(dto, email);
        return new ResponseDto.Empty();
    }
}
