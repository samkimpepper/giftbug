package com.pretchel.pretchel0123jwt.modules.account.service;

import com.pretchel.pretchel0123jwt.infra.RedisApi;
import com.pretchel.pretchel0123jwt.infra.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.global.exception.InvalidInputException;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.global.util.CookieUtils;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.response.LoginTokenDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.LoginDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.ExpiredTokenException;
import com.pretchel.pretchel0123jwt.modules.account.exception.InvalidTokenException;
import com.pretchel.pretchel0123jwt.modules.account.exception.UserAlreadyExistsException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Authority;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserResponseDto;
import com.pretchel.pretchel0123jwt.modules.notification.event.PasswordChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisApi redisApi;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Users findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void signUp(UserSignupDto userSignupDto) {
        if(userRepository.existsByEmail(userSignupDto.getEmail())) {
            Users user = userRepository.findByEmail(userSignupDto.getEmail()).orElseThrow(NotFoundException::new);
            log.info(user.getEmail() + " " + user.getPhoneNumber());

            throw new UserAlreadyExistsException();
        }

        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse(userSignupDto.getBirthday()).getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
            throw new InvalidInputException();
        }

        Users user = Users.builder()
                .email(userSignupDto.getEmail())
                .password(passwordEncoder.encode(userSignupDto.getPassword()))
                .birthday(date)
                .phoneNumber(userSignupDto.getPhoneNumber())
                .gender(userSignupDto.getGender())
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        userRepository.save(user);
    }

    @Transactional
    public LoginTokenDto login(LoginDto dto, HttpServletResponse response) {
        Users user = userRepository.findByEmail(dto.getEmail()).orElseThrow(NotFoundException::new);

        UsernamePasswordAuthenticationToken authenticationToken = dto.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisApi.setValues("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime());

        int cookieMaxAge = tokenInfo.getRefreshTokenExpirationTime().intValue() / 60;
        CookieUtils.addCookie(response, REFRESH_TOKEN, tokenInfo.getRefreshToken(), cookieMaxAge);

        eventPublisher.publishEvent(new PasswordChangedEvent(user, user.getModifiedDate()));
        return new LoginTokenDto(tokenInfo.getAccessToken());
    }

//    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
//        if(!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
//            return responseDto.fail("Refresh Token 정보가 유효하지 않다능 앙", HttpStatus.BAD_REQUEST);
//        }
//
//        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());
//
//        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
//        if(ObjectUtils.isEmpty(refreshToken)) {
//            return responseDto.fail("잘못된 요청", HttpStatus.BAD_REQUEST);
//        }
//        if(!refreshToken.equals(reissue.getRefreshToken())) {
//            return responseDto.fail("Refresh Token 정보가 일치하지 않다능 앙", HttpStatus.BAD_REQUEST);
//        }
//
//        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
//
//        redisTemplate.opsForValue()
//                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
//
//        return responseDto.success(tokenInfo, "Token 정보 갱신됨", HttpStatus.OK);
//    }

    // 액세스 토큰만 재발급
    public String reissue2(String accessToken, String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String userEmail = authentication.getName();
        String refreshTokenRedis = redisApi.getValues(userEmail);
        if(ObjectUtils.isEmpty(refreshTokenRedis)) {
            throw new ExpiredTokenException();
        }
        if(!refreshTokenRedis.equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        return jwtTokenProvider.generateAccessToken(authentication);
    }

    public void logout(String accessToken) {
        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        String userEmail = authentication.getName();

        // 리프레시 삭제

        if(redisApi.getValues("RT:" + userEmail) != null) {
            redisApi.delete("RT:" + userEmail);
        }

        // 쿠키에서도 리프레시 삭제는 컨트롤러에서 해야되나

        // 액세스 토큰은 로그아웃된 토큰으로 처리
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisApi.setValues(accessToken, "logout", expiration);

        SecurityContextHolder.clearContext();
    }







}
