package com.pretchel.pretchel0123jwt;

import com.pretchel.pretchel0123jwt.v1.account.controller.UsersApiController;
import com.pretchel.pretchel0123jwt.v1.account.dto.user.UserRequestDto;
import com.pretchel.pretchel0123jwt.v1.account.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
