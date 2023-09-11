package com.pretchel.pretchel0123jwt.modules.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.UserFactory;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.info.AddressAccountFactory;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventApiControllerTest {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressAccountFactory addressAccountFactory;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventFactory eventFactory;


    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;


    @BeforeEach
    void setup() throws ParseException {
        userFactory.createUser("duck12@gmail.com");
    }


    @AfterEach
    void clean() {
        eventRepository.deleteAll();
        accountRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    public void event_pagination_success() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        addressAccountFactory.createAccount("김오리", user, true);
        addressAccountFactory.createAddress("김오리", user, true);
        generateMultipleEvent(user);
        Event event = eventRepository.findAllByUsers(user).get(0);

        mvc.perform(get("/api/event/page/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageInfo.currentPageNum").value(1))
                .andExpect(jsonPath("$.data.pageInfo.blockLastPageNum").value(2))
                .andExpect(jsonPath("$.data.pageInfo.totalEventCount").value(17))
                .andExpect(jsonPath("$.data.eventList.length()").value(12))
                .andExpect(jsonPath("$.data.eventList[0].nickname").value("오리17"));

        // TODO: 오리1이 젤 먼저나와야.. 젤 일찍 나온 애가. 근데 DESC가 아니라 뭘 해야하지? 애초에 DESC가 뭐지? 나중에 하자^^
    }

    private void generateMultipleEvent(Users user) throws Exception{
        eventFactory.createEvent(user, "오리1", "2023-07-07");
        eventFactory.createEvent(user, "오리2", "2023-07-07");
        eventFactory.createEvent(user, "오리3", "2023-07-07");
        eventFactory.createEvent(user, "오리4", "2023-07-07");
        eventFactory.createEvent(user, "오리5", "2023-07-07");
        eventFactory.createEvent(user, "오리6", "2023-07-07");
        eventFactory.createEvent(user, "오리7", "2023-07-07");
        eventFactory.createEvent(user, "오리8", "2023-07-07");
        eventFactory.createEvent(user, "오리9", "2023-07-07");
        eventFactory.createEvent(user, "오리10", "2023-07-07");
        eventFactory.createEvent(user, "오리11", "2023-07-07");
        eventFactory.createEvent(user, "오리12", "2023-07-07");
        eventFactory.createEvent(user, "오리13", "2023-07-07");
        eventFactory.createEvent(user, "오리14", "2023-07-07");
        eventFactory.createEvent(user, "오리15", "2023-07-07");
        eventFactory.createEvent(user, "오리16", "2023-07-07");
        eventFactory.createEvent(user, "오리17", "2023-07-07");
    }

    @Test
    @WithMockCustomUser
    @DisplayName("디폴트 주소나 계좌 둘 중 하나라도 설정하지 않으면 이벤트 생성 불가")
    void createEventFail() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        addressAccountFactory.createAccount("김오리", user, true);

        mvc.perform(
                multipart("/api/event")
                        .param("nickName", "김실패")
                        .param("eventType", "생일")
                        .param("deadLine", "2022-11-09")
        ).andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithMockCustomUser
    @DisplayName("디폹트 주소 설정해서 이벤트 생성 성공")
    void createEventSuccess() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        addressAccountFactory.createAccount("김오리", user, true);
        addressAccountFactory.createAddress("김오리", user, true);

        MockMultipartFile[] images = new MockMultipartFile[2];
        images[0] = mockMultipartUpload();

        // 이렇게하면 안 됨
//        mvc.perform(post("/api/event")
//                .content(content)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .accept(MediaType.APPLICATION_JSON)
//        ).andDo(print()).andExpect(status().isOk());

        mvc.perform(
                multipart("/api/event")
                        .file(images[0])
                        .param("nickName", "김성공")
                        .param("eventType", "생일")
                        .param("deadLine", "2022-11-09")
        ).andDo(print())
                .andExpect(status().isOk());

        List<Event> events = eventRepository.findAllByUsers(user);
        assertNotNull(events.get(0));
        assertEquals("김성공", events.get(0).getNickname());
    }

//    @Test
//    @WithMockCustomUser
//    void createGift() throws Exception {
//        List<EventListDto> eventList = eventService.getMyEvents("duck12@gmail.com");
//        eventId = eventList.get(0).getId();
//        user = usersRepository.findByEmail("duck12@gmail.com").orElseThrow();
//
//        Account account = Account.builder()
//        .name("김둘기")
//        .accountNum("1002255123456")
//        .bank("우리")
//        .bankCode("09")
//        .birthday("이거왜필요한거지")
//        .users(user)
//        .build();
//
//        Address address = Address.builder()
//                .name("김둘기")
//                .postCode("01234")
//                .roadAddress("둘기로 1길 23")
//                .detailAddress("비둘기아파트 404호")
//                .phoneNum("01012345678")
//                .isDefault(true)
//                .users(user)
//                .build();
//
//        accountId = accountRepository.save(account).getId();
//        addressId = addressRepository.save(address).getId();
//        user.setDefaultAccount(account);
//        user.setDefaultAddress(address);
//        usersRepository.save(user);
//
//        GiftCreateDto dto = GiftCreateDto.builder()
//                .eventId(eventId)
//                .accountId(accountId)
//                .addressId(addressId)
//                .name("애플펜슬")
//                .price(100000)
//                .link("www.naver.com")
//                .story("필기할 때 요긴하게 쓰려고 합니다.")
//                .build();
//        String content = mapper.writeValueAsString(dto);
//
//        mvc.perform(
//                post("/api/gift")
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//        ).andDo(print())
//                .andExpect(status().isOk());
//    }



    // gift에 속해있는 address는 삭제 불가임. 이ㅣ런건 어케 하지?
    // 일단 여기선 gift 테스트 빼서 성공임
    @Test
    @WithMockCustomUser
    @DisplayName("이벤트 삭제(선물이 없기 때문에 성공)")
    void deleteEventSuccess() throws Exception {
        Users user = userRepository.findByEmail("duck12@gmail.com").orElseThrow();
        addressAccountFactory.createAccount("오리오리", user, true);
        addressAccountFactory.createAddress("오리오리", user, true);
        Event event = eventFactory.createEvent(user, "오리오리", "2022-11-25");

        mvc.perform(
                delete("/api/event/" + event.getId())
        ).andDo(print())
        .andExpect(status().isOk());

        List<Event> events = eventRepository.findAllByUsers(user);
        assertFalse(events.contains(event));
    }


    // MultipartFile 객체 만드는 로직 ===================================================================================
    private MockMultipartFile mockMultipartUpload() throws IOException {
        String fileName = "fluffy-cow";
        String contentType = "jpg";
        String filePath = "src/test/resources/images/fluffy-cow.jpg";
        MockMultipartFile mockFile = getMockMultipartFile(fileName, contentType, filePath);

        String getFileName = mockFile.getOriginalFilename().toLowerCase();
        return mockFile;

//        MatcherAssert.assertThat(getFileName, is(fileName.toLowerCase() + "." + contentType));
//        System.out.println(getFileName);
//        System.out.println(fileName);
    }

    MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }
}