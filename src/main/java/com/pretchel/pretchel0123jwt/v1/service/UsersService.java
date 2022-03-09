package com.pretchel.pretchel0123jwt.v1.service;

import com.pretchel.pretchel0123jwt.entity.*;
import com.pretchel.pretchel0123jwt.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.util.CookieUtils;
import com.pretchel.pretchel0123jwt.v1.dto.account.AccountRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.address.AddressRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.profile.ProfileMapping;
import com.pretchel.pretchel0123jwt.v1.dto.user.Response;
import com.pretchel.pretchel0123jwt.v1.dto.user.UserRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.user.UserResponseDto;
import com.pretchel.pretchel0123jwt.v1.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.pretchel.pretchel0123jwt.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

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
    private final ProfileRepository profileRepository;

    @Transactional
    public Users getUsersByEmail(UserDetails userDetails) {
        String email = userDetails.getUsername();

        Optional<Users> usersOptional = usersRepository.findByEmail(email);
        if(usersOptional.isPresent()) {
            return usersOptional.get();
        } else {
            return null;
        }
    }

    @Transactional
    public ResponseEntity<?> signUp(UserRequestDto.SignUp signUp) {
        if(usersRepository.existsByEmail(signUp.getEmail())) {
            return responseDto.fail("이미 회원가입된 이메일임", HttpStatus.BAD_REQUEST);
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

        return responseDto.success("회원가입 성공");
    }

    @Transactional
    public ResponseEntity<?> login(UserRequestDto.Login login, HttpServletResponse response) {
        if(usersRepository.findByEmail(login.getEmail()).orElse(null) == null) {
            return responseDto.fail("존재하지 않는 이메일", HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        int cookieMaxAge = tokenInfo.getRefreshTokenExpirationTime().intValue() / 60;

        CookieUtils.addCookie(response, REFRESH_TOKEN, tokenInfo.getRefreshToken(), cookieMaxAge);

        return responseDto.success(tokenInfo, "로그인 완료", HttpStatus.OK);
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
            return responseDto.fail("해당하는 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
        Users users = usersOptional.get();
        users.update(date, update.getPhoneNumber());

        return responseDto.success("수정 성공");
    }

    @Transactional
    public ResponseEntity<?> updatePassword(UserRequestDto.UpdatePw updatePw, @AuthenticationPrincipal UserDetails userDetails) {
        Users users = getUsersByEmail(userDetails);
        if(users == null) {
            return responseDto.fail("해당하는 유저가 존재하지 않는다능", HttpStatus.BAD_REQUEST);
        }

        if(passwordEncoder.matches(updatePw.getPassword(), users.getPassword())) {
            if(!updatePw.getNewPassword().equals(updatePw.getCheckPassword())) {
                return responseDto.fail("비번이랑 체크비번 일치하지 않음", HttpStatus.BAD_REQUEST);
            }
            users.updatePassword(passwordEncoder.encode(updatePw.getNewPassword()));
            return responseDto.success("비번 변경 완료");
        }
        return responseDto.fail("틀린 비번임", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
        if(!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            return responseDto.fail("Refresh Token 정보가 유효하지 않다능 앙", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if(ObjectUtils.isEmpty(refreshToken)) {
            return responseDto.fail("잘못된 요청", HttpStatus.BAD_REQUEST);
        }
        if(!refreshToken.equals(reissue.getRefreshToken())) {
            return responseDto.fail("Refresh Token 정보가 일치하지 않다능 앙", HttpStatus.BAD_REQUEST);
        }

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return responseDto.success(tokenInfo, "Token 정보 갱신됨", HttpStatus.OK);
    }

    public ResponseEntity<?> logout(String accessToken, String refreshToken) {
        if(!jwtTokenProvider.validateToken(accessToken)) {
            return responseDto.fail("잘못된 요청입니다. accessToken 값이 이상함.", HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        if(redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:" + authentication.getName());
        }

        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        SecurityContextHolder.clearContext();

        return responseDto.success("로그아웃 완료");
    }

    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Users users = getUsersByEmail(userDetails);
        if(users == null) {
            return responseDto.fail("해당하는 유저가 존재하지 않는다능", HttpStatus.BAD_REQUEST);
        }

        List<ProfileMapping> profiles = profileRepository.findProfilesByUserId(users);

        return responseDto.success(profiles, "프로필들임", HttpStatus.OK);
    }

    public ResponseEntity<?> sendEmail(String email) {
        Optional<Users> usersOptional = usersRepository.findByEmail(email);
        if(usersOptional.isEmpty()) {
            return responseDto.fail("존재하지 않는 이메일", HttpStatus.BAD_REQUEST);
        }

        Users users = usersOptional.get();

        confirmPasswordCodeService.sendEmailConfirmCode(users.getId(), users.getEmail());

        return responseDto.success("이메일 전송 완료");
    }

    public ResponseEntity<?> confirmEmail(String authCode) {
        Optional<ConfirmPasswordCode> optionalFindCode = confirmPasswordCodeService.findByIdAndExpiryDateAfterAndExpired(authCode);
        if(!optionalFindCode.isPresent()) {
            return responseDto.fail("잘못된 인증 코드", HttpStatus.NOT_FOUND);
        }
        ConfirmPasswordCode findCode = optionalFindCode.get();
        findCode.setExpired();

        Optional<Users> usersOptional = usersRepository.findById(findCode.getUserId());
        Users users = usersOptional.get();

        return responseDto.success("유효한 인증코드");
    }

    @Transactional
    public ResponseEntity<?> createAddress(AddressRequestDto.Save save, String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("잘못된 유저 이메일"));

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

        return responseDto.success("주소 생성 성공");
    }

    @Transactional
    public ResponseEntity<?> createAccount(AccountRequestDto.Save save, String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("잘못된 유저 이메일"));

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

        return responseDto.success("계좌 생성 완료");
    }

}
