package com.pretchel.pretchel0123jwt.modules.deposit;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingDeposit;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenbankingDepositService {
    private final OpenbankingDepositRepository openbankingDepositRepository;

    public void save(OpenbankingStatus status, OpenbankingDepositResponseDto dto, Gift gift, Users receiver) {

        OpenbankingDeposit deposit = OpenbankingDeposit.of(dto, gift, receiver, status);

        openbankingDepositRepository.save(deposit);
    }

    @Transactional
    public void preSave(String bankTranId, Gift gift, Users receiver) {
        OpenbankingDeposit deposit = OpenbankingDeposit.builder()
                .bank_tran_id(bankTranId)
                .amount(-1)
                .tran_dtime("Unchecked")
                .tran_no("Unchecked")
                .status(OpenbankingStatus.UNCHECKED)
                .receiver(receiver)
                .gift(gift)
                .build();

        openbankingDepositRepository.save(deposit);
    }

    public List<OpenbankingDeposit> findAllByStatus(OpenbankingStatus status) {
        return openbankingDepositRepository.findAllByStatus(status);
    }

    // createdAt 컬럼의 DESC 순대로 정렬
    public List<OpenbankingDeposit> findAllByGiftDESC(Gift gift) {
        return openbankingDepositRepository.findAllByGift(gift, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
