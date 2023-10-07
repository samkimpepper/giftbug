package com.pretchel.pretchel0123jwt.modules.payment;

import com.pretchel.pretchel0123jwt.modules.payments.iamport.service.PaymentMessageTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentJdbcRepositoryTest {

    @Autowired
    private PaymentMessageTestService paymentMessageTestService;

    @Test
    public void testBatchUpdatePayment() throws Exception {
        paymentMessageTestService.batchUpdatePaymentAndMessagePerAllGifts();
    }
}
