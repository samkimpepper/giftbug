package com.pretchel.pretchel0123jwt.modules.info;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UserSettingService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

/*
* Address 여러개 추가하고, Default 설정 잘 되는지 테스트하는 건
* 단위테스트로.
* */

@SpringBootTest
@AutoConfigureMockMvc
public class AddressAccountControllerTest {
    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;


    @BeforeEach
    void setup() throws Exception {
        userFactory.createUser("duck12@gmail.com");
    }

    @AfterEach
    void clean() {
        accountRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
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

        mvc.perform(get("/api/address"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name", is("김둘기")));
    }


    @Test
    @WithMockCustomUser
    void createAccount() throws Exception {
        AccountCreateDto dto = AccountCreateDto.builder()
                .name("김둘기")
                .accountNum("1002255123456")
                .bank("우리")
                .bankCode("09")
                .birthday("19990102")
                .isDefault(true)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/account")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Users user = userRepository.findByEmailFetchJoinAccounts("duck12@gmail.com").orElseThrow();
        Account account = user.getDefaultAccount();
        assertThat(account.getName(), equalTo("김둘기"));
        assertThat(account.getIsDefault(), equalTo(true));
        //assertThat(userSettingService.getDefaultAccount(user), equalTo(account));
    }

    @Test
    @WithMockCustomUser
    public void createMultipleAccountSuccess() throws Exception {
        AccountCreateDto dto = AccountCreateDto.builder()
                .name("김둘기")
                .accountNum("1002255123456")
                .bank("우리")
                .bankCode("09")
                .birthday("19990102")
                .isDefault(true)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/account")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        dto = AccountCreateDto.builder()
                .name("김참새")
                .accountNum("1002255654321")
                .bank("신한")
                .bankCode("02")
                .birthday("20000101")
                .isDefault(true)
                .build();
        content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/account")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Users user = userRepository.findByEmailFetchJoinAccounts("duck12@gmail.com").orElseThrow();
        Account defaultAccount = user.getDefaultAccount();
        Account account = user.getAccounts().get(0);
        assertThat(defaultAccount.getName(), equalTo("김참새"));
        assertThat(account.getIsDefault(), equalTo(false));
        assertThat(account.getName(), equalTo("김둘기"));
    }

    @Test
    @WithMockCustomUser
    public void createMultipleAddressSuccess() throws Exception{
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

        dto = AddressCreateDto.builder()
                .name("김참새")
                .postCode("05678")
                .roadAddress("참새로 2길 2")
                .detailAddress("참새빌라 404호")
                .phoneNum("01098765432")
                .isDefault(true)
                .build();
        content = mapper.writeValueAsString(dto);

        mvc.perform(
                        post("/api/address")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());

        Users user = userRepository.findByEmailFetchJoinAddresses("duck12@gmail.com").orElseThrow();
        Address defaultAddress = user.getDefaultAddress();
        Address address = user.getAddresses().get(0);
        assertThat(defaultAddress.getName(), equalTo("김참새"));
        assertThat(address.getName(), equalTo("김둘기"));
        assertThat(address.getIsDefault(), equalTo(false));

    }

}
