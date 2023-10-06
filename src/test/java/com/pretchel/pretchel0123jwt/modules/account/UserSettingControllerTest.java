package com.pretchel.pretchel0123jwt.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UpdatePasswordDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UpdateUserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.notification.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserSettingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @WithMockCustomUser
    void update_user_info_success() throws Exception {
        userFactory.createUser("duck12@gmail.com");

        UpdateUserInfoDto dto = UpdateUserInfoDto.builder()
                .birthday("1999-01-01")
                .phoneNumber("010-1234-5678")
                .build();

        String content = mapper.writeValueAsString(dto);

        mvc.perform(put("/api/user/update")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        LocalDate expectedDate = LocalDate.parse("1999-01-01", DateTimeFormatter.ISO_DATE);
        assertEquals(user.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), expectedDate);
        assertEquals(user.getPhoneNumber(), "010-1234-5678");
    }

    @Test
    @WithMockCustomUser
    void update_password_success() throws Exception {
        userFactory.createUser("duck12@gmail.com");

        UpdatePasswordDto dto = UpdatePasswordDto.builder()
                .password("password")
                .checkPassword("drakedrake12")
                .newPassword("drakedrake12")
                .build();

        String content = mapper.writeValueAsString(dto);

        mvc.perform(put("/api/user/update-password")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();

        boolean isMatches = passwordEncoder.matches("drakedrake12", user.getPassword());
        assertTrue(isMatches);
    }



}
