package com.pretchel.pretchel0123jwt.v1.account.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmPasswordCode {
    private static final long EXPIRY_TIME = 5L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column
    private LocalDateTime expiryDate;

    @Column
    private boolean expired;

    @Column
    private String userId;

    public static ConfirmPasswordCode create(String userId) {
        ConfirmPasswordCode confirmPasswordCode = new ConfirmPasswordCode();
        confirmPasswordCode.expiryDate = LocalDateTime.now().plusMinutes(EXPIRY_TIME); // 5분 후 만료
        confirmPasswordCode.userId = userId;
        confirmPasswordCode.expired = false;
        return confirmPasswordCode;
    }

    public void setExpired() {
        expired = true;
    }
}
