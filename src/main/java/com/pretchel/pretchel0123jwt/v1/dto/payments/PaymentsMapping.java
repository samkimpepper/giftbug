package com.pretchel.pretchel0123jwt.v1.dto.payments;

import java.time.LocalDateTime;

public interface PaymentsMapping {
    String getBuyerName();
    int getAmount();
    String getMessage();
    LocalDateTime getPaidAt();
}
