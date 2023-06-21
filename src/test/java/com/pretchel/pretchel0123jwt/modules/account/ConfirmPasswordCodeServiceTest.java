package com.pretchel.pretchel0123jwt.modules.account;

import com.pretchel.pretchel0123jwt.infra.EmailSender;
import com.pretchel.pretchel0123jwt.modules.account.domain.ConfirmPasswordCode;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.ConfirmPasswordCodeRepository;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.ConfirmPasswordCodeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConfirmPasswordCodeServiceTest {
    @Autowired
    private ConfirmPasswordCodeService confirmPasswordCodeService;

    @MockBean
    private EmailSender emailSender;

    @Autowired
    private ConfirmPasswordCodeRepository confirmPasswordCodeRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() throws Exception {
        userFactory.createUser("duck34@gmail.com");
    }

    @Test
    @Transactional
    public void confirmed_code_should_be_expired() {
        Users user = userRepository.findByEmail("duck34@gmail.com").orElse(null);
        confirmPasswordCodeService.sendEmail(user);

        ConfirmPasswordCode authCode = confirmPasswordCodeRepository.findByUserId(user.getId()).orElse(null);
        assertNotNull(authCode);

        confirmPasswordCodeService.confirmEmail(authCode.getId());
        assertTrue(authCode.isExpired());

    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
        confirmPasswordCodeRepository.deleteAll();
    }
}
