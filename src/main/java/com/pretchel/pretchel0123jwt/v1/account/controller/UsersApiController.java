package com.pretchel.pretchel0123jwt.v1.account.controller;

import com.pretchel.pretchel0123jwt.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.util.CookieUtils;
import com.pretchel.pretchel0123jwt.util.Helper;
import com.pretchel.pretchel0123jwt.v1.account.dto.account.AccountRequestDto;
import com.pretchel.pretchel0123jwt.v1.event.dto.address.AddressRequestDto;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.v1.account.dto.user.UserRequestDto;
import com.pretchel.pretchel0123jwt.v1.event.service.AccountService;
import com.pretchel.pretchel0123jwt.v1.event.service.AddressService;
import com.pretchel.pretchel0123jwt.v1.account.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.pretchel.pretchel0123jwt.v1.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UsersApiController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersService usersService;
    private final Response responseDto;
    private final AddressService addressService;
    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Validated UserRequestDto.SignUp signUp, Errors errors) {
        if(errors.hasErrors()) {
            return responseDto.invalidFields(Helper.refineErrors(errors));
        }

        return usersService.signUp(signUp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated UserRequestDto.Login login,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   Errors errors) {
        if(errors.hasErrors()) {
            return responseDto.invalidFields(Helper.refineErrors(errors));
        }

        return usersService.login(login, response);

//        CookieUtils.deleteCookie(request, response, ACCESS_TOKEN);
//        CookieUtils.addCookie(response, ACCESS_TOKEN, tokenInfo.getAccessToken(), 60 * 60 * 24 * 7);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("accesstoken") String accesstoken,
                                    @RequestHeader("refreshtoken") String refreshtoken) {
        //return usersService.logout(logout);
        //log.info(headers.toSingleValueMap().toString());

        return usersService.logout(accesstoken, refreshtoken);
    }



    @GetMapping("/test")
    public ResponseEntity<?> test(@AuthenticationPrincipal UserDetails userDetails) {
        return responseDto.success(userDetails.getUsername(), "userDetails???", HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestHeader @Validated UserRequestDto.Reissue reissue) {
        System.out.println(reissue.getAccessToken());
        // ?????? Authorization ????????? ????????? ???????????? ?????????
        // ??????????????? ?????? ???????????? ????????? ?????????????



        return usersService.reissue(reissue);
    }

    @GetMapping("/reissue")
    public ResponseEntity<?> reissue2(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7);
        } else {
            responseDto.fail("????????? ?????????????????? ?????????", HttpStatus.NOT_FOUND);
        }

        Optional<Cookie> cookieOptional = CookieUtils.getCookie(request, REFRESH_TOKEN);
        if(cookieOptional.isEmpty()) {
            responseDto.fail("????????? ????????????????????? ?????? ????????????????????????", HttpStatus.NOT_FOUND);
        }
        Cookie cookie = cookieOptional.get();

        return usersService.reissue2(accessToken, cookie.getValue());
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequestDto.Update update) {
        return usersService.update(update);
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UserRequestDto.UpdatePw updatePw, @AuthenticationPrincipal UserDetails userDetails) {
        return usersService.updatePassword(updatePw, userDetails);
    }

    @PostMapping("/find-email")
    public ResponseEntity<?> findPassword(@RequestBody String email) {

        return usersService.sendEmail(email);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestBody String authCode) {

        return usersService.confirmEmail(authCode);
    }

    @PostMapping("/address")
    public ResponseEntity<?> createAddress(@RequestBody AddressRequestDto.Save save, @AuthenticationPrincipal UserDetails userDetails) {
        return usersService.createAddress(save, userDetails.getUsername());
    }


    @PostMapping("/account")
    public ResponseEntity<?> createAccount(@RequestBody AccountRequestDto.Save save, @AuthenticationPrincipal UserDetails userDetails) {
        return usersService.createAccount(save, userDetails.getUsername());
    }

    @GetMapping("/address")
    public ResponseEntity<?> getAllMyAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        return addressService.getAllMyAddresses(userDetails);
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAllMyAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        return accountService.getAllMyAccounts(userDetails);
    }

}
