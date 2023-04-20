package com.pretchel.pretchel0123jwt.modules.scheduler.test;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.FindTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FindTaskTestController {
    private final FindTask task;

    @GetMapping("/test/find/expired")
    public ResponseDto.Empty testFindExpiredGifts() {
        task.findExpiredGifts();
        return new ResponseDto.Empty();
    }

    @GetMapping("/test/find/completed")
    public ResponseDto.Empty testFindCompletedGifts() {
        task.findCompletedGifts();
        return new ResponseDto.Empty();
    }
}
