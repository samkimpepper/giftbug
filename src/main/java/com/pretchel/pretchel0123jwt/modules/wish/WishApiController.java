package com.pretchel.pretchel0123jwt.modules.wish;

import com.pretchel.pretchel0123jwt.global.ResponseDto;
import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftListDto;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish")
public class WishApiController {
    private final WishService wishService;
    private final UserRepository userRepository;
    private final GiftRepository giftRepository;

    @PostMapping
    public ResponseDto.Empty wish(@RequestParam String giftId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);

        wishService.create(user, gift);
        return new ResponseDto.Empty();
    }
    @DeleteMapping
    public ResponseDto.Empty unWish(@RequestParam String giftId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        Gift gift = giftRepository.findById(giftId).orElseThrow(NotFoundException::new);

        wishService.delete(user, gift);
        return new ResponseDto.Empty();
    }

    @GetMapping
    public ResponseDto.DataList<GiftListDto> getMyWishList() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        return new ResponseDto.DataList<>(wishService.getWishList(user));
    }
}
