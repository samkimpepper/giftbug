package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftJdbcRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GiftTestService {

    private final GiftJdbcRepository giftJdbcRepository;
    private final GiftRepository giftRepository;

    private final EventRepository eventRepository;

    private final AccountRepository accountRepository;

    private final AddressRepository addressRepository;
    private final String[] giftNames = {"이어폰", "마우스", "지갑", "카드지갑", "캔들", "갤럭시", "아이폰", "매거진랙", "만년필"
    , "키보드", "모니터", "카메라", "바디로션", "향수", "스니커즈", "러닝화", "티셔츠", "핸드크림", "닌텐도", "조명", "손목시계", "탁상시계"
    , "이불", "자켓", "슬랙스", "아이패드", "로퍼"};
    private int[] prices = new int[35];

    @Transactional
    public void createAllGifts() {
        List<Event> events = eventRepository.findAll();
        List<Gift> gifts = new ArrayList<>();
        for(Event event : events) {
            Users user = event.getUsers();
            Account account = accountRepository.findAllByUsers(user).get(0);
            Address address = addressRepository.findAllByUsers(user).get(0);
            Gift gift = create(event, account, address);
            gifts.add(gift);
        }

        giftJdbcRepository.batchUpdateGifts(gifts);
    }

    public Gift create(Event event, Account account, Address address) {
        generateRandomPrices();
        Random random = new Random();
        int rndGiftNames = random.nextInt(giftNames.length);
        int rndPrices = random.nextInt(prices.length);

        Gift gift = Gift.builder()
                .name(giftNames[rndGiftNames])
                .price(prices[rndPrices])
                .deadLine(event.getDeadLine())
                .funded(0)
                .link("google.com")
                .story("스토리")
                .state(GiftState.ongoing)
                .event(event)
                .account(account)
                .address(address)
                .build();

        return gift;

        //giftRepository.save(gift);
    }

    private void generateRandomPrices() {
        int price = 20000;
        for(int i=0; i<35; i++) {
            prices[i] = price;
            price += 5000;
        }
    }
}
