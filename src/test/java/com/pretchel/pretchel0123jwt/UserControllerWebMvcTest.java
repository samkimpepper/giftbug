package com.pretchel.pretchel0123jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.modules.account.controller.UserController;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerWebMvcTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Test
    void signup() throws Exception {
        UserSignupDto dto = UserSignupDto.builder()
                .email("duck12@gmail.com")
                .password("password")
                .checkPassword("password")
                .birthday("2022-08-21")
                .phoneNumber("01012345678")
                .gender("FEMALE")
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/user/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
