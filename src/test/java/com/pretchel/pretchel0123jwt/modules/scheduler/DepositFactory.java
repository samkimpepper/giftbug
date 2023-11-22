package com.pretchel.pretchel0123jwt.modules.scheduler;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.deposit.OpenbankingDepositService;
import com.pretchel.pretchel0123jwt.modules.deposit.domain.OpenbankingStatus;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.OpenbankingDepositResponseDto;
import com.pretchel.pretchel0123jwt.modules.deposit.dto.ResListDto;
import com.pretchel.pretchel0123jwt.modules.gift.GiftService;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.ProcessState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DepositFactory {
    @Autowired
    private OpenbankingDepositService depositService;

    @Autowired
    private GiftService giftService;

    @Transactional
    public void createDeposit(OpenbankingStatus status, int amount, Gift gift, Users receiver) {
        OpenbankingDepositResponseDto dto = generateOpenbankingDepositResponseDto("A0000", amount);
        depositService.save(status, dto, gift, receiver);
        giftService.syncSetProcessState(gift, ProcessState.completed);
        System.out.println("gift state: " + gift.getProcessState());
    }
    private OpenbankingDepositResponseDto generateOpenbankingDepositResponseDto(String bank_rsp_code, int amount) {
        ResListDto resList = generateResListDto(bank_rsp_code, amount);
        List<ResListDto> resLists = new ArrayList<>();
        resLists.add(resList);

        return OpenbankingDepositResponseDto.builder()
                .api_tran_id("2ffd133a-d17a-431d-a6a5")
                .api_tran_dtm("202306161010102937")
                .rsp_code("A0000")
                .rsp_message("응답 메시지")
                .wd_bank_code_std("097")
                .wd_bank_code_sub("1230001")
                .wd_bank_name("우리은행")
                .wd_account_num_masked("000-1230000-***")
                .wd_print_content("대금입금")
                .wd_account_holder_name("이채림")
                .res_cnt("1")
                .res_list(resLists)
                .build();
    }

    private ResListDto generateResListDto(String bank_rsp_code, int amount) {
        return ResListDto.builder()
                .tran_no("1")
                .bank_tran_id("F123456789U4BC34239Z")
                .bank_tran_date("20230616")
                .bank_rsp_code(bank_rsp_code)
                .account_alias("급여계좌")
                .bank_code_std("097")
                .bank_code_sub("1230001")
                .bank_name("우리은행")
                .account_num_masked("000-1230000-***")
                .print_content("대금입금")
                .account_holder_name("김오리")
                .tran_amt(String.valueOf(amount))
                .cms_num("93848103221")
                .build();

    }
}
