package com.pretchel.pretchel0123jwt;

import com.pretchel.pretchel0123jwt.entity.Authority;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.controller.api.UsersApiController;
import com.pretchel.pretchel0123jwt.v1.dto.user.UserRequestDto;
import com.pretchel.pretchel0123jwt.v1.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
@WebMvcTest(controllers = UsersApiController.class)
public class UsersLogicTest {
    @Autowired
    private UsersService usersService;


    @Test
    @Transactional
    public void signUp() {
        UserRequestDto.SignUp userRequestDto = new UserRequestDto.SignUp();
        userRequestDto.setEmail("glutwind@naver.com");
        userRequestDto.setPassword("hello12345!!");
        userRequestDto.setBirthday("1999-05-04");
        userRequestDto.setGender("FEMALE");
        userRequestDto.setPhoneNumber("01012345678");

        usersService.signUp(userRequestDto);

        //Users users = usersService.getUsersByEmail(userRequestDto.getEmail());

    }
}
