package com.pretchel.pretchel0123jwt.modules.scheduler.test;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.DepositTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DepositTaskTestController {
    private final DepositTask task;

    /* 테스트 결과는 로그로 확인 */

    @GetMapping("/test/deposit")
    public ResponseDto.Empty testDepositExpiredGiftAmount() {
        task.depositExpiredGiftAmount();
        return new ResponseDto.Empty();
    }

    @GetMapping("/test/check")
    public ResponseDto.Empty testCheckDepositResult() {
        task.checkDepositResult();
        return new ResponseDto.Empty();
    }
}
