package com.pretchel.pretchel0123jwt.modules.account.service;

import com.pretchel.pretchel0123jwt.global.exception.InvalidInputException;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.response.UserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyPasswordDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.PasswordNotMatchException;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountInfoDto;
import com.pretchel.pretchel0123jwt.modules.notification.event.PasswordChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserSettingService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void update(ModifyInfoDto dto, String email) {
        Date date = null;
        if(dto.getBirthday() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = new Date(sdf.parse(dto.getBirthday()).getTime());
            } catch(ParseException ex) {
                ex.printStackTrace();
                throw new InvalidInputException();
            }
        }

        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        user.update(date, dto.getPhoneNumber());
    }

    @Transactional
    public void updatePassword(ModifyPasswordDto dto, String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            if(!dto.getNewPassword().equals(dto.getCheckPassword())) {
                throw new PasswordNotMatchException();
            }
            user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));

            LocalDateTime modifiedAt = user.getModifiedDate();
            eventPublisher.publishEvent(new PasswordChangedEvent(user, modifiedAt));

            return;
        }
        throw new PasswordNotMatchException();
    }

    public UserInfoDto getUserInfo(String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        AccountInfoDto account = null;
        if(users.getDefaultAccount() != null) {
            account = AccountInfoDto.fromAccount(users.getDefaultAccount());
        }

        return UserInfoDto.fromUser(users, account);

    }
}
