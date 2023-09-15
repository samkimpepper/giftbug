package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gifts")
public class PopularGiftController {
    private final GiftService giftService;

    @GetMapping("/most-supported")
    public ResponseDto.DataList<GiftListDto> getMostSupportedGifts() {
        return new ResponseDto.DataList<>(giftService.getMostSupportedGifts());
    }

    @GetMapping("/most-wished")
    public ResponseDto.DataList<GiftListDto> getMostWishedGifts() {
        return new ResponseDto.DataList<>(giftService.getMostWishedGifts());
    }
}
