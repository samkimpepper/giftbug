package com.pretchel.pretchel0123jwt.v1.account.service;

import com.pretchel.pretchel0123jwt.v1.account.domain.ConfirmPasswordCode;
import com.pretchel.pretchel0123jwt.v1.account.repository.ConfirmPasswordCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConfirmPasswordCodeService {
    private final ConfirmPasswordCodeRepository confirmPasswordCodeRepository;
    private final EmailSenderService emailSenderService;

    // 이메일 인증 코드 생성
    public String sendEmailConfirmCode(String userId, String receiverEmail) {
        ConfirmPasswordCode emailCode = ConfirmPasswordCode.create(userId);
        confirmPasswordCodeRepository.save(emailCode);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("비밀번호 변경 인증 코드");
        mailMessage.setText(emailCode.getId());
        emailSenderService.sendEmail(mailMessage);

        return emailCode.getId();
    }

    public Optional<ConfirmPasswordCode> findByIdAndExpiryDateAfterAndExpired(String codeId) {
        Optional<ConfirmPasswordCode> confirmPasswordCode = confirmPasswordCodeRepository.findByIdAndExpiryDateAfterAndExpired(codeId, LocalDateTime.now(), false);
        return confirmPasswordCode;
    }
}
