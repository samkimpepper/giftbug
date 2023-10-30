package com.pretchel.pretchel0123jwt.modules.payments.iamport.service;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageService;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IamportMessageService {
    private final IamportPaymentService iamportPaymentService;
    private final MessageService messageService;
    private final GiftService giftService;
    private final GiftRepository giftRepository;


    @Transactional
    public void createPaymentNMessage(PaymentsCompleteDto dto, Users user, Gift gift) {
        IamportPayment payment = iamportPaymentService.createPayment(dto, user, gift); // IamportPayment 엔티티 save
        messageService.createMessage(payment, user, gift);
        giftService.fund(gift, payment.getAmount());
    }


    @Transactional
    public void syncCreatePaymentNMessage(PaymentsCompleteDto dto, Users user, Gift gift) {
        IamportPayment payment = iamportPaymentService.createPayment(dto, user, gift); // IamportPayment 엔티티 save
        messageService.createMessage(payment, user, gift);
        giftService.syncFund(gift, payment.getAmount());
    }

    /*
    * createPaymentNMessage()에 @Transactional 붙이면, payment가 영속성컨텍스트에 저장이 안 돼서
    * Message 엔티티 저장할때 에러남. (transient 어쩌고)
    * 그래서 ChatGPT가 추천한 대로 중첩 트랜잭션 해봤는데도 안 돼서
    * 그냥 차악의 방법인 각각 트랜잭션 분리해서 실행하기로 함.
    * (최악은 Message가 payment를 nullable로 참조하는 것)
    *
    *
    * */
}
