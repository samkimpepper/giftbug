package com.pretchel.pretchel0123jwt.modules.account.service;

import com.pretchel.pretchel0123jwt.global.exception.InvalidInputException;
import com.pretchel.pretchel0123jwt.infra.EmailSender;
import com.pretchel.pretchel0123jwt.modules.account.domain.ConfirmPasswordCode;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.repository.ConfirmPasswordCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConfirmPasswordCodeService {
    private final ConfirmPasswordCodeRepository confirmPasswordCodeRepository;
    private final EmailSender emailSender;

    public void sendEmail(Users user) {
        sendEmailConfirmCode(user.getId(), user.getEmail());
    }

    public void confirmEmail(String authCode) {

        ConfirmPasswordCode recvCode = findByIdAndExpiryDateAfterAndExpired(authCode).orElseThrow(InvalidInputException::new);
        recvCode.setExpired();
    }

    // 이메일 인증 코드 생성
    private String sendEmailConfirmCode(String userId, String receiverEmail) {
        ConfirmPasswordCode emailCode = ConfirmPasswordCode.create(userId);
        confirmPasswordCodeRepository.save(emailCode);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("비밀번호 변경 인증 코드");
        mailMessage.setText(emailCode.getId());
        emailSender.sendEmail(mailMessage);

        return emailCode.getId();
    }

    private Optional<ConfirmPasswordCode> findByIdAndExpiryDateAfterAndExpired(String codeId) {
        return confirmPasswordCodeRepository.findByIdAndExpiryDateAfterAndExpired(codeId, LocalDateTime.now(), false);
    }


}
