package com.pretchel.pretchel0123jwt.modules.gift.domain;

/*
* ongoing: 마감기한 안 지남 + 돈 안 참
* completed: 마감기한 안 지남 + 돈 참
* expired: 마감기한 지남 + 돈 안 참
* finished: 마감기한 안 지남 + 돈 안 참. 근데 사용자가 마감함. (일종의, 강제적 expired임)
*   finished는 그냥 없애는게 나을 것 같아 도메인에서는.
* canceled: 사용자가 취소함
*
* 즉, 사용자가 요청할 수 있는 건 finish와 cancel
* */

/*
* TODO: 이거 정책 다시 생각해야겠다. 일단 펀딩 성공한(completed) 선물을 따로 옮겨둘 도메인 또 만들어야하나.
*  그리고 CompletedGift와 이름 겹쳐서 헷갈림. CompletedGift의 Completed는 펀딩금액 다 찼다는 뜻이 아니라
* 만료든 펀딩 성공이든, 그에대한 처리가 완료된, 이라는 뜻인데... 이름 잘못 지음.
* */

public enum GiftState {
    ongoing,
    success,
    expired,
    canceled
}
