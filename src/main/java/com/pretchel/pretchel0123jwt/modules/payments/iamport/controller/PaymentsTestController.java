package com.pretchel.pretchel0123jwt.modules.payments.iamport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentsTestController {
    @GetMapping("/test/payments-page")
    public String paymentsPage() {
        return "payment";
    }
}
