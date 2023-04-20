package com.pretchel.pretchel0123jwt.modules.scheduler.test;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.MoveTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MoveTaskTestController {
    private final MoveTask task;

    @GetMapping("/test/move")
    public ResponseDto.Empty moveToCompletedGift() {
        task.moveToCompletedGift();
        return new ResponseDto.Empty();
    }
}
