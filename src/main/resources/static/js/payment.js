'use strict'

var MERCHANT_UID;
let index = {
    init: function() {
        $("#purchase").on("click", () => {
            this.requestPay();
        });

        $("#get_merchant_uid").on("click", () => {
            this.requestMerchantUid();
        });
    },

    requestMerchantUid: function () {
        $.ajax({
            type:"POST",
            url:"/test/payments/pre-create",
            error: function (status, error) {
                console.log(error);
                console.log(status);
            },
            success: function(res) {
                console.log(res.data);
                MERCHANT_UID = res.data;
            },
        });
    },

    requestPay: function() {
        var IMP = window.IMP;
        IMP.init("imp52948958");

        IMP.request_pay({
            pg : 'kakaopay',
            pay_method : 'card', //생략 가능
            merchant_uid: MERCHANT_UID, // 상점에서 관리하는 주문 번호
            name : '주문명:결제테스트', // 결제창에서 보여지는 이름
            amount : 14000,
            buyer_email : 'yourside19@gmail.com',
            buyer_name : '오리친구',
            buyer_tel : '010-9470-2977',
            buyer_addr : '서울특별시 강남구 삼성동',
            buyer_postcode : '01234'
        }, function (rsp) {
            if(rsp.success) {
                let data = {
                    impUid: rsp.imp_uid,
                    merchantUid: rsp.merchant_uid,
                    paidAmount: 14000,
                    buyerName: "오리친구",
                    isMember: false,
                    buyerEmail: null,
                    giftId: ""
                }

                $.ajax({
                    type: "POST",
                    url: "/api/payments/complete",
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    data: JSON.stringify(data)
                }).done(function(data) {
                    console.log(data);
                })
            } else {
                alert("결제 실패. 에러: " + rsp.error_msg);
            }
        });
    }
}
index.init();