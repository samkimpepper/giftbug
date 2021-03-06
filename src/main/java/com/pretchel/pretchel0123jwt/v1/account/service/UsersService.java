package com.pretchel.pretchel0123jwt.v1.account.service;

import com.pretchel.pretchel0123jwt.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.util.CookieUtils;
import com.pretchel.pretchel0123jwt.v1.account.dto.user.UserEventsDto;
import com.pretchel.pretchel0123jwt.v1.event.domain.Account;
import com.pretchel.pretchel0123jwt.v1.account.domain.Authority;
import com.pretchel.pretchel0123jwt.v1.account.domain.ConfirmPasswordCode;
import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
import com.pretchel.pretchel0123jwt.v1.account.dto.account.AccountRequestDto;
import com.pretchel.pretchel0123jwt.v1.event.domain.Event;
import com.pretchel.pretchel0123jwt.v1.event.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.v1.account.repository.UsersRepository;
import com.pretchel.pretchel0123jwt.v1.event.domain.Address;
import com.pretchel.pretchel0123jwt.v1.event.dto.address.AddressRequestDto;
import com.pretchel.pretchel0123jwt.v1.event.dto.event.EventMapping;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.v1.account.dto.user.UserRequestDto;
import com.pretchel.pretchel0123jwt.v1.account.dto.user.UserResponseDto;
import com.pretchel.pretchel0123jwt.v1.event.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.v1.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pretchel.pretchel0123jwt.v1.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final Response responseDto;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;
    private final EmailSenderService emailSenderService;
    private final ConfirmPasswordCodeService confirmPasswordCodeService;
    private final AddressRepository addressRepository;
    private final AccountRepository accountRepository;
    private final EventRepository profileRepository;

    @Transactional
    public Users getUsersByEmail(UserDetails userDetails) {
        String email = Optional.ofNullable(userDetails.getUsername()).orElseThrow(NotFoundException::new);

        return usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public ResponseEntity<?> signUp(UserRequestDto.SignUp signUp) {
        if(usersRepository.existsByEmail(signUp.getEmail())) {
            return responseDto.fail("?????? ??????????????? ????????????", HttpStatus.BAD_REQUEST);
        }

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse(signUp.getBirthday()).getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
        }

        Users user = Users.builder()
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .birthday(date)
                .phoneNumber(signUp.getPhoneNumber())
                .gender(signUp.getGender())
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        usersRepository.save(user);

        return responseDto.success("???????????? ??????");
    }

    @Transactional
    public ResponseEntity<?> login(UserRequestDto.Login login, HttpServletResponse response) {
        if(usersRepository.findByEmail(login.getEmail()).orElse(null) == null) {
            return responseDto.fail("???????????? ?????? ?????????", HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        int cookieMaxAge = tokenInfo.getRefreshTokenExpirationTime().intValue() / 60;

        CookieUtils.addCookie(response, REFRESH_TOKEN, tokenInfo.getRefreshToken(), cookieMaxAge);

        return responseDto.success(tokenInfo, "????????? ??????", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> update(UserRequestDto.Update update) {

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse(update.getBirthday()).getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
        }

        Optional<Users> usersOptional = usersRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!usersOptional.isPresent()){
            return responseDto.fail("???????????? ????????? ???????????? ????????????.", HttpStatus.BAD_REQUEST);
        }
        Users users = usersOptional.get();
        users.update(date, update.getPhoneNumber());

        return responseDto.success("?????? ??????");
    }

    @Transactional
    public ResponseEntity<?> updatePassword(UserRequestDto.UpdatePw updatePw, @AuthenticationPrincipal UserDetails userDetails) {
        Users users = getUsersByEmail(userDetails);
        if(users == null) {
            return responseDto.fail("???????????? ????????? ???????????? ????????????", HttpStatus.BAD_REQUEST);
        }

        if(passwordEncoder.matches(updatePw.getPassword(), users.getPassword())) {
            if(!updatePw.getNewPassword().equals(updatePw.getCheckPassword())) {
                return responseDto.fail("???????????? ???????????? ???????????? ??????", HttpStatus.BAD_REQUEST);
            }
            users.updatePassword(passwordEncoder.encode(updatePw.getNewPassword()));
            return responseDto.success("?????? ?????? ??????");
        }
        return responseDto.fail("?????? ?????????", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
        if(!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            return responseDto.fail("Refresh Token ????????? ???????????? ????????? ???", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if(ObjectUtils.isEmpty(refreshToken)) {
            return responseDto.fail("????????? ??????", HttpStatus.BAD_REQUEST);
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            return responseDto.fail("Refresh Token ????????? ???????????? ????????? ???", HttpStatus.BAD_REQUEST);
        }

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return responseDto.success(tokenInfo, "Token ?????? ?????????", HttpStatus.OK);
    }

    // ????????? ????????? ?????????
    public ResponseEntity<?> reissue2(String accessToken, String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            return responseDto.fail("Refresh Token ????????? ???????????? ????????? ???", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        String refreshTokenRedis = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if(ObjectUtils.isEmpty(refreshTokenRedis)) {
            return responseDto.fail("????????? ??????????????? ???????????? ???????????? ?????????????????????", HttpStatus.BAD_REQUEST);
        }
        if(!refreshTokenRedis.equals(refreshToken)) {
            return responseDto.fail("Refresh Token ????????? ???????????? ????????? ???", HttpStatus.BAD_REQUEST);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        return responseDto.success(newAccessToken, "??? ??????????????????", HttpStatus.OK);
    }

    public ResponseEntity<?> logout(String accessToken, String refreshToken) {
        if(!jwtTokenProvider.validateToken(accessToken)) {
            return responseDto.fail("????????? ???????????????. accessToken ?????? ?????????.", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // ???????????? ??????
        if(redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // ??????????????? ???????????? ????????? ?????????????????? ????????????

        // ????????? ????????? ??????????????? ???????????? ??????
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        SecurityContextHolder.clearContext();

        return responseDto.success("???????????? ??????");
    }

    public List<UserEventsDto> getUserEvents(@AuthenticationPrincipal UserDetails userDetails) {
        Users users = getUsersByEmail(userDetails);

        List<EventMapping> profiles = profileRepository.findProfilesByUserId(users);

        List<Event> events = profileRepository.findAllByUsers(users);

        return events.stream()
                .map(event -> {
                    return UserEventsDto.fromEvent(event);
                })
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> sendEmail(String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        confirmPasswordCodeService.sendEmailConfirmCode(users.getId(), users.getEmail());

        return responseDto.success("????????? ?????? ??????");
    }

    public ResponseEntity<?> confirmEmail(String authCode) {
        Optional<ConfirmPasswordCode> optionalFindCode = confirmPasswordCodeService.findByIdAndExpiryDateAfterAndExpired(authCode);
        if(!optionalFindCode.isPresent()) {
            return responseDto.fail("????????? ?????? ??????", HttpStatus.NOT_FOUND);
        }
        ConfirmPasswordCode findCode = optionalFindCode.get();
        findCode.setExpired();

        Optional<Users> usersOptional = usersRepository.findById(findCode.getUserId());
        Users users = usersOptional.get();

        return responseDto.success("????????? ????????????");
    }

    @Transactional
    public ResponseEntity<?> createAddress(AddressRequestDto.Save save, String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("????????? ?????? ?????????"));

        Address address = Address.builder()
                .name(save.getName())
                .postCode(save.getPostCode())
                .roadAddress(save.getRoadAddress())
                .detailAddress(save.getDetailAddress())
                .phoneNum(save.getPhoneNum())
                .users(users)
                .build();

        addressRepository.save(address);
        if(save.getIsDefault()) {
            users.setDefaultAddress(address);
        }

        return responseDto.success("?????? ?????? ??????");
    }

    @Transactional
    public ResponseEntity<?> createAccount(AccountRequestDto.Save save, String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("????????? ?????? ?????????"));

        Account account = Account.builder()
                .name(save.getName())
                .accountNum(save.getAccountNum())
                .bank(save.getBank())
                .bankCode(save.getBankCode())
                .birthday(save.getBirthday())
                .users(users)
                .build();

        accountRepository.save(account);

        if(save.getIsDefault()) {
            users.setDefaultAccount(account);
        }

        return responseDto.success("?????? ?????? ??????");
    }

}
