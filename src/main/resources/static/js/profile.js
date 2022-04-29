'use strict'

let index = {
    init: function() {
        $("#btn-submit").on("click", () => {
            this.save();
        });
    },

    save: function() {
        let data = {
            nickname: $("#nickname").val(),
            postcode: $("#postcode").val(),
            roadAddress: $("#roadAddress").val(),
            detailAddress: $("#detailAddress").val(),
            eventType: $("#eventType").val()
        }
        $.ajax({
            type: "POST",
            url: "/api/profile",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            beforeSend: function(xhr) {
                var accessToken = localStorage.getItem("accessToken");
                xhr.setRequestHeader("accessToken", accessToken);
            },
            error: function(xhr, status, error) {
                console.log(status);
                console.log(error);
            },
            success: function(res) {
                console.log(res);
            },
        });
    },

    // reIssue: function() {
    //    재발급 요청 어케 하지.. beforeSend로 해야되나?
    // }
}

index.init();