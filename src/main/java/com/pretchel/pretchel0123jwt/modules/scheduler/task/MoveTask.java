package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.CompletedGiftRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoveTask {
    private final MoveTaskTransactional moveTaskTransactional;
    private final GiftRepository giftRepository;

    /*
     * state가 ongoing이 아니고, processState가 success인 애들 대상으로
     * Gift -> CompletedGift로 옮김.
     * */
    public void moveToCompletedGift() {
        List<Gift> gifts = giftRepository.findAllByStateNotInAndProcessStateIn(GiftState.ongoing, ProcessState.completed);

        for(Gift gift: gifts) {
            moveTaskTransactional.moveToCompletedGift(gift);
        }
    }
}
