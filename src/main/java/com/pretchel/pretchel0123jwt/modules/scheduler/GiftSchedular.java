package com.pretchel.pretchel0123jwt.modules.scheduler;

import com.pretchel.pretchel0123jwt.modules.scheduler.task.DepositTask;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.FindTask;
import com.pretchel.pretchel0123jwt.modules.scheduler.task.MoveTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GiftSchedular {
    private final DepositTask depositTask;
    private final FindTask findTask;
    private final MoveTask moveTask;


    /*
    * FindGiftTask
    * MoveGiftTask
    * DepositExpiredGiftTask
    * */

    // 만료된 선물 있는지(마감기한으로) 검사
    // 매일 00시 00분 00초에 실행
    @Scheduled(cron="0 0 0 * * *")
    public void findExpiredGifts() {
        findTask.findExpiredGifts();
    }

    // TODO: 은행 점검 없는 다른 시간대로 변경 예정(상의해야됨)
    @Scheduled(cron="0 2 0 * * *")
    public void depositExpiredGifts() {
        depositTask.depositExpiredGiftAmount();
    }

    // 위 입금이체 시도에서 실패했을 가능성 고려해 2분 뒤에 입금이체 결과 확인 요청
    @Scheduled(cron = "0 4 0 * * *")
    public void checkDepositResult() {
        depositTask.checkDepositResult();
    }


    // 위 입금이체 결과 확인 요청에서 실패했을 가능성 고려해 4분 뒤에 입금이체 재요청
    // (왜냐하면 입금이체 결과 확인 실패의 모든 경우는 그냥 입금이체 재요청으로 처리해야됨)
    @Scheduled(cron = "0 8 0 * * *")
    public void retryDepositExpiredGifts() {
        depositTask.depositExpiredGiftAmount();
    }

    // 위 입금이체 재요청이 실패했을 가능성 고려해 2분 뒤에 입금이체 재요청의 결과 확인 요청
    // 여기서 실패한다면 끝. 다음날에 시도함.
    @Scheduled(cron = "0 10 0 * * *")
    public void retryCheckDepositResult() {
        depositTask.checkDepositResult();
    }

}
