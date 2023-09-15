package com.pretchel.pretchel0123jwt.modules.wish;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/wish")
public class WishTestController {
    private final WishTestService wishTestService;

    @PostMapping
    public ResponseDto.Empty generateTestWishes() {
        wishTestService.generateTestWishes();
        return new ResponseDto.Empty();
    }
}
