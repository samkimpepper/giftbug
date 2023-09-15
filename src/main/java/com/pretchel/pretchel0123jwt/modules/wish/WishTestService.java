package com.pretchel.pretchel0123jwt.modules.wish;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WishTestService {
    private final WishRepository wishRepository;
    private final GiftRepository giftRepository;
    private final UserRepository userRepository;

    private final WishService wishService;

    @Transactional
    public void generateTestWishes() {
        List<Users> users = userRepository.findAll();
        List<Gift> gifts = giftRepository.findTop1000ByOrderByIdDesc();
        int giftsLength = gifts.size();
        Random random = new Random();

        for(Users user: users) {
            for (int i=0; i<10; i++) {
                Gift gift = gifts.get(random.nextInt(giftsLength));
                wishService.create(user, gift);
            }
        }
    }
}
