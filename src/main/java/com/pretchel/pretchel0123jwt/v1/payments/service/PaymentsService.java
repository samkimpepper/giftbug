//package com.pretchel.pretchel0123jwt.v1.payments.service;
//
//
//import com.pretchel.pretchel0123jwt.v1.event.domain.Gift;
//import com.pretchel.pretchel0123jwt.v1.payments.domain.Payments;
//import com.pretchel.pretchel0123jwt.v1.payments.domain.PaymentsStatus;
//import com.pretchel.pretchel0123jwt.v1.account.domain.Users;
//import com.pretchel.pretchel0123jwt.v1.payments.dto.PaymentsRequestDto;
//import com.pretchel.pretchel0123jwt.global.Response;
//import com.pretchel.pretchel0123jwt.v1.event.repository.GiftRepository;
//import com.pretchel.pretchel0123jwt.v1.payments.repository.PaymentsRepository;
//import com.pretchel.pretchel0123jwt.v1.account.repository.UsersRepository;
//import com.pretchel.pretchel0123jwt.v1.event.service.GiftService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentsService {
//    private final PaymentsRepository paymentsRepository;
//    private final UsersRepository usersRepository;
//    private final GiftRepository giftRepository;
//
//    private final GiftService giftService;
//
//    private final Response responseDto;
//
//    @Transactional
//    public ResponseEntity<?> save(PaymentsRequestDto.Save save) {
//        Optional<Users> usersOptional;
//        Users users = null;
//        if(save.getIsMember()) {
//            usersOptional = usersRepository.findByEmail(save.getEmail());
//            users = usersOptional.get();
//        }
//
//        Optional<Gift> optionalGift = giftRepository.findById(save.getGiftId());
//        if(optionalGift.isEmpty()) {
//            responseDto.fail("선물을 찾을 수 없음", HttpStatus.NOT_FOUND);
//        }
//        Gift gift = optionalGift.get();
//
//        Payments payments = Payments.builder()
//                .amount(save.getAmount())
//                .buyerName(save.getBuyerName())
//                .message(save.getMessage())
//                .isMember(save.getIsMember())
//                .gift(gift)
//                .users(users)
//                .build();
//
//        String merchant_uid = paymentsRepository.save(payments).getMerchant_uid();
//
//        return responseDto.success(merchant_uid, "merchant_uid임", HttpStatus.OK);
//    }
//
//    // 사용자의 결제 내역
//
//
//    // 결제 성공
//    @Transactional
//    public ResponseEntity<?> complete(PaymentsRequestDto.Complete complete) {
//        Optional<Payments> optionalPayments = paymentsRepository.findById(complete.getMerchantUid());
//        if(optionalPayments.isEmpty()) {
//            responseDto.fail("merchant_id가 잘못됐나봄 못찾겠음", HttpStatus.NOT_FOUND);
//        }
//        Payments payments = optionalPayments.get();
//
//        payments.setImp_uid(complete.getImpUid());
//
//        Optional<Gift> optionalGift = giftRepository.findById(payments.getGift().getId());
//        if(optionalGift.isEmpty()) {
//            responseDto.fail("gift 못찾겠음 사라졌나봄", HttpStatus.NOT_FOUND);
//        }
//        Gift gift = optionalGift.get();
//
//        int remainder = gift.getRemainder();
//        gift.setRemainder(remainder - complete.getPaidAmount());
//        if(gift.getRemainder() <= 0) {
//            return giftService.complete(gift);
//        }
//
//        return responseDto.success("결제 후처리 성공");
//    }
//
//    // 결제 실패
//    @Transactional
//    public ResponseEntity<?> fail(PaymentsRequestDto.Complete complete) {
//        Optional<Payments> optionalPayments = paymentsRepository.findById(complete.getMerchantUid());
//        if(optionalPayments.isEmpty()) {
//            responseDto.fail("merchant_id가 잘못됐나봄 못찾겠음", HttpStatus.NOT_FOUND);
//        }
//        Payments payments = optionalPayments.get();
//        payments.setStatus(PaymentsStatus.FAILED);
//        payments.setFaildAt(LocalDateTime.now());
//
//        return responseDto.fail("결제 정보가 일치하지 않아서 결제 실패함", HttpStatus.BAD_REQUEST);
//    }
//}
