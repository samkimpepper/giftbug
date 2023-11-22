package com.pretchel.pretchel0123jwt.modules.scheduler.task;

import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositRepository;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.gift.domain.CompletedGift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.CompletedGiftRepository;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.repository.IamportPaymentRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.Message;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MoveTaskTransactional {
    private final GiftRepository giftRepository;
    private final CompletedGiftRepository completedGiftRepository;

    private final MessageRepository messageRepository;

    private final IamportPaymentRepository iamportPaymentRepository;

    private final OpenbankingDepositRepository depositRepository;

    @Transactional
    public void moveToCompletedGift(Gift gift) {
        CompletedGift completedGift;
        try {
            completedGift = createCompletedGift(gift);
        } catch (Exception ex) {
            return;
        }

        List<Message> messages = messageRepository.findAllByGift(gift);
        CompletableFuture<Void> messagesFuture = CompletableFuture.runAsync(() ->
            messages.parallelStream().forEach(message -> message.moveToCompletedGift(completedGift))
        );

        List<IamportPayment> payments = iamportPaymentRepository.findAllByGift(gift);
        CompletableFuture<Void> paymentsFuture = CompletableFuture.runAsync(() ->
            payments.parallelStream().forEach(payment -> payment.moveToCompletedGift(completedGift))
        );

        List<OpenbankingDeposit> deposits = depositRepository.findAllByGift(gift);
        CompletableFuture<Void> depositsFuture = CompletableFuture.runAsync(() ->
            deposits.parallelStream().forEach(deposit -> deposit.moveToCompletedGift(completedGift))
        );

        CompletableFuture.allOf(messagesFuture, paymentsFuture, depositsFuture)
                        .thenRun(() -> giftRepository.delete(gift))
                        .join();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private CompletedGift createCompletedGift(Gift gift) {
        return completedGiftRepository.save(CompletedGift.fromGift(gift));
    }

//    @Transactional
//    public void moveToCompletedGift(Gift gift) {
//        CompletedGift completedGift = CompletedGift.fromGift(gift);
//        completedGiftRepository.save(completedGift);
//
//        List<Message> messages = messageRepository.findAllByGift(gift);
//        for(Message message: messages) {
//            message.moveToCompletedGift(completedGift);
//        }
//
//        List<IamportPayment> payments = iamportPaymentRepository.findAllByGift(gift);
//        for(IamportPayment payment: payments) {
//            payment.moveToCompletedGift(completedGift);
//        }
//
//        List<OpenbankingDeposit> deposits = depositRepository.findAllByGift(gift);
//        for(OpenbankingDeposit deposit: deposits) {
//            deposit.moveToCompletedGift(completedGift);
//        }
//
//        giftRepository.delete(gift);
//    }
}
