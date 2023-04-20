package com.pretchel.pretchel0123jwt.modules.gift.domain;

/*
* success: 결제/환불 처리 성공
* check: 결제/환불 처리 결과 조회 필요
* none: 다시 결제/환불해야함 + 아직 결제/환불 처리 안됨 아무튼 결제를 해야한다는 뜻
* */
public enum ProcessState {

    // TODO: 이거 success가 아니라 completed로 하는게 나을듯.
    completed,
    check,
    none
}
