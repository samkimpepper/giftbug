package com.pretchel.pretchel0123jwt.modules.event.dto.event;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class EventPagingDto {
    private Map<String, Object> pageInfo;
    private List<EventListDto> eventList;
}
