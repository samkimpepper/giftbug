package com.pretchel.pretchel0123jwt.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.UserAlreadyExistsException;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PasswordFindControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws Exception {
        UserSignupDto dto = UserSignupDto.builder()
                .email("duck12@gmail.com")
                .password("password")
                .checkPassword("password")
                .birthday("2022-08-21")
                .phoneNumber("01099999999")
                .gender("FEMALE")
                .build();

        //usersService.signUp(dto);
        String content = mapper.writeValueAsString(dto);

        try {
            mvc.perform(post("/api/user/signup")
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (UserAlreadyExistsException ex) {

        }
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    void getUserEvents() throws Exception {

        mvc.perform(get("/api/user/my-events"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void getUserInfo() throws Exception {
        mvc.perform(get("/api/user/user-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is("duck12@gmail.com")));
    }

    @Test
    void findEmail() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow(NotFoundException::new);
        System.out.println(user.getEmail());

        Map<String, String> content = new HashMap<>();
        content.put("email", "duck12@gmail.com");

        mvc.perform(post("/api/user/find-email")
                .content(mapper.writeValueAsString(content))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
