package com.pretchel.pretchel0123jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.infra.global.exception.InvalidInputException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.LoginDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.repository.UsersRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersApiControllerTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private static final String BASE_URL = "/api/user";
    private static final String EMAIL = "duck12@gmail.com";

    Users user;

//    @Test
//    void signUp() throws Exception {
//        // given
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date(sdf.parse("2022-08-21").getTime());
//
//        user = Users.builder()
//            .email("duck12@gmail.com")
//            .password(encoder.encode("password"))
//            .birthday(date)
//            .phoneNumber("01012345678")
//            .gender("FEMALE")
//            .roles(Collections.singletonList(Authority.ROLE_USER.name()))
//            .build();
//
//        user = usersRepository.save(user);
//    }

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

        Users user = usersRepository.findByEmail("duck12@gmail.com").orElseThrow();
        assertNotNull(user);
        assertNotEquals(user.getPassword(), "password");
    }

    @Test
    void login() throws Exception {
        LoginDto dto = LoginDto.builder()
                .email("duck12@gmail.com")
                .password("password")
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post(BASE_URL + "/login")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("duck12@gmail.com"));
    }

    

    @Test
    @WithMockCustomUser
    void updateUserInfo() throws Exception {
        ModifyInfoDto dto = ModifyInfoDto.builder()
                .birthday(null)
                .phoneNumber("01098765432")
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(put(BASE_URL + "/update")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print()).andExpect(status().isOk());
    }

    // 생일을 yyyy-MM-dd가 아니라 yyyy.MM.dd 처럼 잘못된 형식으로 입력할 시
    @Test
    @WithMockCustomUser
    void updateInvalidBirthday() throws Exception {
        ModifyInfoDto dto = ModifyInfoDto.builder()
                .birthday("1999.01.01")
                .phoneNumber("01098765432")
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(put(BASE_URL + "/update")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(getApiResultExceptionClass(result)).isEqualTo(InvalidInputException.class);
                });
    }

    // 폰번호 정규표현식 찾아야겠다
    @Test
    @WithMockCustomUser
    void updateInvalidPhoneNumber() throws Exception {
        ModifyInfoDto dto = ModifyInfoDto.builder()
                .birthday("1999-01-01")
                .phoneNumber(null)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(put(BASE_URL + "/update")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }


    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

}