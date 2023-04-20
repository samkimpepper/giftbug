package com.pretchel.pretchel0123jwt.modules.payments.iamport.service;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.message.MessageService;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.dto.PaymentsCompleteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IamportMessageService {
    private final IamportPaymentService iamportPaymentService;
    private final MessageService messageService;

    @Transactional
    public void createPaymentNMessage(PaymentsCompleteDto dto, Users user, Gift gift) {
        IamportPayment payment = iamportPaymentService.createPayment(dto, user, gift);
        messageService.createMessage(payment);
    }
}
