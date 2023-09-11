package com.pretchel.pretchel0123jwt.modules.wish;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
