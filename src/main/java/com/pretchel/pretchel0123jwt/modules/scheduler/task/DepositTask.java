package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositRepository;
import com.pretchel.pretchel0123jwt.infra.OpenbankingApi;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositService;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.DepositResultCheckResponseDto;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.scheduler.test.DepositTaskTestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepositTask {
    private final GiftRepository giftRepository;
    private final OpenbankingApi openbankingApi;


    private final OpenbankingDepositService openbankingDepositService;

    private final UserRepository userRepository;

    private final DepositTaskTransactional depositTaskTransactional;


    /*
     * 입금이체 에러 처리
     * 400, 803, 804: 그냥 에러.
     * 822: 은행거래고유번호 중복. 다시 만들어서 해야됨;; -> 이것만 입금이체 재요청.
     * A0007: 에러.
     * 400, 803, 804, 822, A0007일 시 OpenbankingStatus는 check로
     * 그 이외는 failed로.
     * */


    @Transactional
    public void depositExpiredGiftAmount() {
        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.none);

        for(Gift gift: gifts) {
                depositTaskTransactional.depositExpiredGiftAmountTransactional(gift);
        }
    }

    // 로직 순서대로 설명
    /*
    * 1. Gift 중, 만료되었고(expired) 잘 처리되었는지 확인해야하는(check) 것만 조회해서 순회함.
    * 2. Gift 각각에 대해, 해당 Gift에 달린 입금이체 내역들(deposits)을 조회해옴.
    * 3. 그런데, 내역들을 최근에 만들어진 순서대로 정렬해서 가져오기 때문에 맨 첫 번째 입금이체 엔티티만 확인할 거임.
    * 4.
    * */
    @Transactional
    public void checkDepositResult() {
        List<Gift> gifts = giftRepository.findAllByStateInAndProcessStateIn(GiftState.expired, ProcessState.check);

        for(Gift gift: gifts) {
            depositTaskTransactional.checkDepositResult(gift);
        }
    }

}
