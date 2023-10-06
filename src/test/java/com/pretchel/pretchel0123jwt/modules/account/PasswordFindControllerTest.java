package com.pretchel.pretchel0123jwt.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.infra.EmailSender;
import com.pretchel.pretchel0123jwt.modules.account.domain.ConfirmPasswordCode;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.UserAlreadyExistsException;
import com.pretchel.pretchel0123jwt.modules.account.repository.ConfirmPasswordCodeRepository;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PasswordFindControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private ConfirmPasswordCodeRepository confirmPasswordCodeRepository;

    @MockBean
    private EmailSender emailSender;


    @Test
    void find_password_success() throws Exception {
        userFactory.createUser("duck12@gmail.com");

        mvc.perform(post("/api/user/find-password")
                .param("email", "duck12@gmail.com"))
                .andExpect(status().isOk())
                .andDo(print());

        String userId = userRepository.findByEmail("duck12@gmail.com").orElseThrow().getId();
        ConfirmPasswordCode confirmPasswordCode = confirmPasswordCodeRepository.findByUserId(userId).orElse(null);
        assertNotNull(confirmPasswordCode);
    }

    @Test
    @Transactional
    void confirm_password_success() throws Exception {
        userFactory.createUser("duck12@gmail.com");

        mvc.perform(post("/api/user/find-password")
                        .param("email", "duck12@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String userId = userRepository.findByEmail("duck12@gmail.com").orElseThrow().getId();
        ConfirmPasswordCode confirmPasswordCode = confirmPasswordCodeRepository.findByUserId(userId).orElse(null);
        String authCode = confirmPasswordCode.getId();

        mvc.perform(post("/api/user/confirm-password")
                        .param("authCode", authCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        confirmPasswordCode = confirmPasswordCodeRepository.findByUserId(userId).orElse(null);
        assertTrue(confirmPasswordCode.isExpired());
        then(emailSender).should().sendEmail(any(SimpleMailMessage.class));

    }

}
