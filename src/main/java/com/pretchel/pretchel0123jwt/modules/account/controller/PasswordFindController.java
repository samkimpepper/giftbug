package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.ConfirmPasswordCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class PasswordFindController {
    private final UserRepository userRepository;
    private final ConfirmPasswordCodeService confirmPasswordCodeService;

    @PostMapping("/find-password")
    public ResponseDto.Empty findPassword(@RequestParam String email) {
        System.out.println(email);
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        confirmPasswordCodeService.sendEmail(user);
        return new ResponseDto.Empty();
    }

    @PostMapping("/confirm-password")
    public ResponseDto.Empty confirmPassword(@RequestParam String authCode) {

        confirmPasswordCodeService.confirmEmail(authCode);
        return new ResponseDto.Empty();
    }
}
