package com.pretchel.pretchel0123jwt.modules.wish;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.dto.GiftListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;

    private final GiftService giftService;

    @Transactional
    public void create(Users user, Gift gift) {
        Wish wish = Wish.builder()
                .user(user)
                .gift(gift)
                .build();

        wishRepository.save(wish);

        giftService.increaseWishCount(gift);
    }

    @Transactional
    public void delete(Users user, Gift gift) {
        Wish wish = wishRepository.findByUserAndGift(user, gift);

        wishRepository.delete(wish);
        giftService.decreaseWishCount(gift);
    }

    public List<GiftListDto> getWishList(Users user) {
        List<Gift> gifts = wishRepository.findGiftsByUser(user);
        return gifts.stream().map(
                gift -> {
                    return GiftListDto.fromGift(gift);
                }
        ).collect(Collectors.toList());
    }
}
