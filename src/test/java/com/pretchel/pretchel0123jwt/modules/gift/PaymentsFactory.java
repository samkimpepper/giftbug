package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.PaymentsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentsFactory {
    @Autowired
    private PaymentsRepository paymentsRepository;

    public String preCreate() {
        Payments payments = Payments.builder()
                .type(PaymentsType.iamport)
                .build();

        payments = paymentsRepository.save(payments);
        return payments.getMerchant_uid();
    }

    @Transactional
    public void createPayments(String merchantUid, int amount, Users buyer, Gift gift) {
        Payments payments = paymentsRepository.findById(merchantUid).orElseThrow();

        payments.setAmount(amount);
        payments.setBuyerEmail(buyer.getEmail());
        payments.setUsers(buyer);
        payments.setGift(gift);
    }
}
