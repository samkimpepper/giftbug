package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.EmptyValueExistsException;
import com.pretchel.pretchel0123jwt.global.util.CookieUtils;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.response.LoginTokenDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.LoginDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
@Tag(name = "사용자 인증")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseDto.Empty signUp(@RequestBody UserSignupDto userSignupDto) {
        userService.signUp(userSignupDto);
        return new ResponseDto.Empty();
    }

    //@ApiOperation(value="로그인")
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseDto.Data<LoginTokenDto> login(@RequestBody @Validated LoginDto dto,
                                                 HttpServletResponse response) {

        return new ResponseDto.Data<>(userService.login(dto, response));
//        CookieUtils.deleteCookie(request, response, ACCESS_TOKEN);
//        CookieUtils.addCookie(response, ACCESS_TOKEN, tokenInfo.getAccessToken(), 60 * 60 * 24 * 7);
    }

    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseDto.Empty logout(@RequestHeader("accesstoken") String accesstoken,
                                    @RequestHeader("refreshtoken") String refreshtoken) {

        userService.logout(accesstoken);
        return new ResponseDto.Empty();
    }

    //@ApiOperation(value = "토큰 재발급")
    @GetMapping("/reissue")
    public ResponseDto.Data<String> reissue(HttpServletRequest request) {
        // 이걸 할 필요가 있나? 어차피 filter에서 하는데
        String accessToken = request.getHeader("Authorization");
        if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7);
        } else {
            throw new EmptyValueExistsException(); // TODO: 토큰이 없다는 예외를 따로 만들어야 하나?
        }

        Optional<Cookie> cookieOptional = CookieUtils.getCookie(request, REFRESH_TOKEN);
        if(cookieOptional.isEmpty()) {
            throw new EmptyValueExistsException();
        }
        Cookie cookie = cookieOptional.get();

        return new ResponseDto.Data<>(userService.reissue2(accessToken, cookie.getValue()));
    }

}
