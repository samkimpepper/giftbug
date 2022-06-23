package com.pretchel.pretchel0123jwt.v1.account.controller;

import com.pretchel.pretchel0123jwt.v1.account.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/signup")
    public String signUp() {
        return "users/users-signup";
    }

    @GetMapping("/login")
    public String login() {
        return "users/users-login";
    }


}
