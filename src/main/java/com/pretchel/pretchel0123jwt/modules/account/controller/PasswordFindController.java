package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.ConfirmPasswordCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.DispatcherServlet;

import javax.validation.constraints.NotNull;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
//@Api(value = "비밀번호 찾기 컨트롤러")
public class PasswordFindController {
    private final UserRepository userRepository;
    private final ConfirmPasswordCodeService confirmPasswordCodeService;

    //@ApiOperation(value = "비밀번호 찾기", notes = "인자로 받은 이메일로 코드 전송")
    //@ApiImplicitParam(value = "이메일", dataType = "String", paramType = "query", required = true)
    @PostMapping("/find-password")
    public ResponseDto.Empty findPassword(@RequestParam String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        confirmPasswordCodeService.sendEmail(user);
        return new ResponseDto.Empty();
    }

    //@ApiOperation(value = "비밀번호 찾기 코드 인증", notes = "이메일로 전송된 코드를 입력하면 인증되어 비밀번호 변경 페이지로 리다이렉트(아직 구현 안 됨)")
    //@ApiImplicitParam(value = "인증 코드", dataType = "String", paramType = "query", required = true)
    @PostMapping("/confirm-password")
    public ResponseDto.Empty confirmPassword(@RequestParam String authCode) {

        confirmPasswordCodeService.confirmEmail(authCode);
        return new ResponseDto.Empty();
    }
}
