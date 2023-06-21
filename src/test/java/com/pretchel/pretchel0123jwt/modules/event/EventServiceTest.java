package com.pretchel.pretchel0123jwt.modules.event;

import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Test
    @WithMockCustomUser
    public void create_event_success() throws Exception {
        EventCreateDto dto = createEventCreateDto();


    }

    private EventCreateDto createEventCreateDto() {
        return EventCreateDto.builder()
                .nickName("김오리")
                .eventType("생일")
                .deadLine("2023-06-30")
                .build();
    }
}
