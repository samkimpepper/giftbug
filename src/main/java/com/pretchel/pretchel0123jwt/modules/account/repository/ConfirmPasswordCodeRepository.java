package com.pretchel.pretchel0123jwt.modules.account.repository;

import com.pretchel.pretchel0123jwt.modules.account.domain.ConfirmPasswordCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmPasswordCodeRepository extends JpaRepository<ConfirmPasswordCode, String> {
    Optional<ConfirmPasswordCode> findByIdAndExpiryDateAfterAndExpired(String id, LocalDateTime now, boolean expired);

    Optional<ConfirmPasswordCode> findByUserId(String userId);
}
