package com.pretchel.pretchel0123jwt.modules.info;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressAccountControllerTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    final String EMAIL = "duck12@gmail.com";

    @BeforeEach
    void setup() throws Exception {
        Users user = userFactory.createUser("duck12@gmail.com");

        userRepository.save(user);

    }

    @Test
    @WithMockCustomUser
    void createAddress() throws Exception {

        AddressCreateDto dto = AddressCreateDto.builder()
                .name("김둘기")
                .postCode("01234")
                .roadAddress("둘기로 1길 23")
                .detailAddress("비둘기아파트 404호")
                .phoneNum("01012345678")
                .isDefault(true)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(
                post("/api/address")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void createAccount() throws Exception {
        AccountCreateDto dto = AccountCreateDto.builder()
                .name("김둘기")
                .accountNum("1002255123456")
                .bank("우리")
                .bankCode("09")
                .birthday("이거왜필요한거지")
                .isDefault(true)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/account")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void getMyAllAddresses() throws Exception {
        mvc.perform(get("/api/address"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name", is("김둘기")));
    }


}
