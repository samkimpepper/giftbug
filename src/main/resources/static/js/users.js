'use strict'

let index = {
    init: function() {
        $("#btn-signup").on("click", () => {
            this.signup();
        });
        $("#btn-login").on("click", () => {
            this.login();
        });
        $("#btn-find").on("click", () => {
            this.findPassword();
        });
    },

    signup: function () {
        let data = {
            email: $("#email").val(),
            password: $("#password").val(),
            gender: "female",
            birthday: $("#birthday").val(),
            phoneNumber: $("#phoneNumber").val()
        }

        $.ajax({
            type: "POST",
            url: "/api/user/signup",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function(res) {

            console.log(res);
        }).fail(function(err) {
            alert(JSON.stringify(err));
        });
    },

    login: function () {
        let data = {
            email: $("#email").val(),
            password: $("#password").val()
        }
        $.ajax({
            type: "POST",
            url: "/api/user/login",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function(res) {
            localStorage.setItem('accessToken', res.data.accessToken);
            localStorage.setItem('refreshToken', res.data.refreshToken);
            location.href="/";
        }).fail(function(err) {
            alert(JSON.stringify(err));
        });
    },

    findPassword: function () {
        let data = {
            email: $("#email").val()
        }
        $.ajax({
            type: "POST",
            url: "/api/user/find",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function(res) {
            location.href="/user/enter-code";
        }).fail(function(err) {
            alert(JSON.stringify(err));
        });
    }

}
index.init();