package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.util.CookieUtils;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.response.LoginTokenDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.LoginDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
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
public class UserController {
    private final UserService userService;
    private final Response responseDto;

    @PostMapping("/signup")
    public ResponseDto.Empty signUp(@RequestBody UserSignupDto userSignupDto) {
        userService.signUp(userSignupDto);
        return new ResponseDto.Empty();
    }

    @PostMapping("/login")
    public ResponseDto.Data<LoginTokenDto> login(@RequestBody @Validated LoginDto dto,
                                                 HttpServletResponse response) {

        return new ResponseDto.Data<>(userService.login(dto, response));
//        CookieUtils.deleteCookie(request, response, ACCESS_TOKEN);
//        CookieUtils.addCookie(response, ACCESS_TOKEN, tokenInfo.getAccessToken(), 60 * 60 * 24 * 7);
    }

    @DeleteMapping("/logout")
    public ResponseDto.Empty logout(@RequestHeader("accesstoken") String accesstoken,
                                    @RequestHeader("refreshtoken") String refreshtoken) {

        userService.logout(accesstoken);
        return new ResponseDto.Empty();
    }

    @GetMapping("/reissue")
    public ResponseDto.Data<String> reissue2(HttpServletRequest request) {
        // 이걸 할 필요가 있나? 어차피 filter에서 하는데
        String accessToken = request.getHeader("Authorization");
        if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7);
        } else {
            responseDto.fail("헤더에 액세스토큰이 없는듯", HttpStatus.NOT_FOUND);
        }

        Optional<Cookie> cookieOptional = CookieUtils.getCookie(request, REFRESH_TOKEN);
        if(cookieOptional.isEmpty()) {
            responseDto.fail("쿠키에 리프레시토큰이 없음 재로그인해야될듯", HttpStatus.NOT_FOUND);
        }
        Cookie cookie = cookieOptional.get();

        return new ResponseDto.Data<>(userService.reissue2(accessToken, cookie.getValue()));
    }

}
