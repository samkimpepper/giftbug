package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.ConfirmPasswordCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class PasswordFindController {
    private final UserRepository userRepository;
    private final ConfirmPasswordCodeService confirmPasswordCodeService;

    @PostMapping("/find-email")
    public ResponseDto.Empty findPassword(@RequestBody String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        confirmPasswordCodeService.sendEmail(user);
        return new ResponseDto.Empty();
    }

    @PostMapping("/confirm-email")
    public ResponseDto.Empty confirmEmail(@RequestBody @NotNull String authCode) {

        confirmPasswordCodeService.confirmEmail(authCode);
        return new ResponseDto.Empty();
    }
}
