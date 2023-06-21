package com.pretchel.pretchel0123jwt.modules.account;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UserFactory {

    @Autowired
    UserService userService;

    public void createUser(String email) throws ParseException {

        UserSignupDto dto = UserSignupDto.builder()
                        .email(email)
                        .password("password")
                        .checkPassword("password")
                        .birthday("1999-06-13")
                        .phoneNumber("010-0000-0000")
                        .gender("FEMALE")
                        .build();

        userService.signUp(dto);
    }
}
