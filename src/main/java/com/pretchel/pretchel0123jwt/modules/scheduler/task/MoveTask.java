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
    private final GiftRepository giftRepository;
    private final CompletedGiftRepository completedGiftRepository;

    private final MessageRepository messageRepository;

    private final IamportPaymentRepository iamportPaymentRepository;

    private final OpenbankingDepositRepository depositRepository;

    /*
     *
     * state가 ongoing이 아니고, processState가 success인 애들 대상으로
     * Gift -> CompletedGift로 옮김.
     *
     *
     * */
    @Transactional
    public void moveToCompletedGift() {
        List<Gift> gifts = giftRepository.findAllByStateNotInAndProcessStateIn(GiftState.ongoing, ProcessState.completed);

        for(Gift gift: gifts) {
            CompletedGift completedGift = CompletedGift.fromGift(gift);
            completedGiftRepository.save(completedGift);

            List<Message> messages = messageRepository.findAllByGift(gift);
            for(Message message: messages) {
                message.moveToCompletedGift(completedGift);
            }

            List<IamportPayment> payments = iamportPaymentRepository.findAllByGift(gift);
            for(IamportPayment payment: payments) {
                payment.moveToCompletedGift(completedGift);
            }

            List<OpenbankingDeposit> deposits = depositRepository.findAllByGift(gift);
            for(OpenbankingDeposit deposit: deposits) {
                deposit.moveToCompletedGift(completedGift);
            }

            giftRepository.delete(gift);
        }
    }
}
