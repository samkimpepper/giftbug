package com.pretchel.pretchel0123jwt.modules.event.service;

import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.global.exception.S3UploadException;
import com.pretchel.pretchel0123jwt.global.util.S3Uploader;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventDetailDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventListDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.ProfileResponseDto;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftListDto;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final GiftRepository giftRepository;
    private final GiftService giftService;
    private final Response responseDto;
    private final S3Uploader s3Uploader;
    private final ModelMapper modelMapper;


    @Transactional
    public void save(EventCreateDto dto, Users user) {

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse(dto.getDeadLine()).getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
        }

        String profileImageUrl = null;
        String backgroundImageUrl = null;
        try {
            try {
                if(dto.imagesCount() > 0 && dto.getImages()[0] != null) {
                    profileImageUrl = s3Uploader.upload(dto.getImages()[0]);
                }
                if(dto.imagesCount() > 1 && dto.getImages()[1] != null) {
                    backgroundImageUrl = s3Uploader.upload(dto.getImages()[1]);
                }
            } catch(NullPointerException ex) {
                log.info("이벤트 생성 과정에서 프사 혹은 배사에서 널 포인터 발생. 근데 널이어도 상관 없음.");
            } finally {
                Event profile = Event.builder()
                        .nickname(dto.getNickName())
                        .eventType(dto.getEventType())
                        .profileImageUrl(profileImageUrl)
                        .backgroundImageUrl(backgroundImageUrl)
                        .deadLine(date)
                        .users(user)
                        .isExpired(false)
                        .build();
                eventRepository.save(profile);
            }

        } catch (IOException ex) {
            throw new S3UploadException();
        }
    }

    public List<EventListDto> getMyEvents(Users user) {

//        List<EventMapping> profiles = eventRepository.findProfilesByUserId(users);
//        List<EventMapping> sortedProfiles = profiles.stream()
//                .sorted(Comparator.comparing(EventMapping::getCreateDate).reversed())
//                .collect(Collectors.toList());

        List<Event> eventList = eventRepository.findAllByUsers(user);
        List<EventListDto> dtoList = eventList.stream()
                .map(EventListDto::fromEvent)
                .collect(Collectors.toList());

        return dtoList.stream()
                .sorted(Comparator.comparing(EventListDto::getCreateDate).reversed())
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> getAllProfiles() {
        List<Event> profiles = eventRepository.findAll();

        return responseDto.success(profiles, "모든 프로필", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAllByOrderByCreateDate(int pageNum, int profilesPerPage) {
        Page<Event> pages = eventRepository.findAll(
                PageRequest.of(pageNum - 1, profilesPerPage, Sort.by(Sort.Direction.DESC, "createDate"))
        );

//        List<ProfileResponseDto.View> sortedProfiles = profiles.stream()
//                .map(ProfileResponseDto.View::new)
//                .collect(Collectors.toList());

        List<Event> profiles = pages.getContent();
        List<ProfileResponseDto.View> profileDtos = profiles.stream()
                .map(profile -> modelMapper.map(profile, ProfileResponseDto.View.class))
                .collect(Collectors.toList());

        return responseDto.success(profileDtos, "모든프로필 근데 맵핑 안함 ㅠㅠ", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public EventDetailDto getDetail(String eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotFoundException::new);
        List<GiftListDto> giftList = giftService.getMyGifts(event);
        return EventDetailDto.fromEvent(event, giftList);
    }


    public Event findById(String eventId) {
        return eventRepository.findById(eventId).orElseThrow(NotFoundException::new);
    }

    public Long count() {
        return eventRepository.count();
    }

    @Transactional
    public ResponseEntity<?> delete(String eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotFoundException::new);

        giftRepository.deleteAllByEvent(event);
        eventRepository.delete(event);

        return responseDto.success("삭제 완료");
    }
}
