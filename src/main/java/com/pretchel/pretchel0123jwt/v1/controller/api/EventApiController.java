package com.pretchel.pretchel0123jwt.v1.controller.api;

import com.pretchel.pretchel0123jwt.util.Paginator;
import com.pretchel.pretchel0123jwt.v1.dto.profile.ProfileRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.user.Response;
import com.pretchel.pretchel0123jwt.v1.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventApiController {

    private final EventService profileService;
    private final Response responseDto;

    private static final Integer PROFILES_PER_PAGE = 12;
    private static final Integer PAGES_PER_BLOCK = 5;

    @PostMapping
    public ResponseEntity<?> save(ProfileRequestDto.Save save,
                                  @AuthenticationPrincipal UserDetails userDetails) {
//        Enumeration params = request.getParameterNames();
//        System.out.println("--------------------------------");
//        while(params.hasMoreElements()) {
//            String name = (String)params.nextElement();
//            System.out.println(name + " : " +request.getParameter(name));
//        }

        return profileService.save(save, userDetails);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<?> getAllProfiles2(@PathVariable("page") Integer page) {
        log.info("페이지:" + page);

        try {
            Paginator paginator = new Paginator(PAGES_PER_BLOCK, PROFILES_PER_PAGE, profileService.count());
            Map<String, Object> pageInfo = paginator.getFixedBlock(page);
            for(String key : pageInfo.keySet()) {
                Object val = pageInfo.get(key);
                System.out.println(key + ": " + val);
            }
        } catch (IllegalStateException ex) {
            log.info("컨트롤러 페이지네이션 좆됨");
        }

        return profileService.findAllByOrderByCreateDate(page, PROFILES_PER_PAGE);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyProfiles(@AuthenticationPrincipal UserDetails userDetails) {
        return profileService.getMyProfiles(userDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getThisEvent(@PathVariable("id") String id) {
        return profileService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") String id) {
        return profileService.delete(id);
    }


//    @PostMapping("/test")
//    public void saveTest(ProfileRequestDto.Save save) {
//        log.info("닉네임:" + save.getNickName());
//        log.info("이벤타입:" + save.getEventType());
//        log.info(save.getImage().getOriginalFilename());
//    }
}
