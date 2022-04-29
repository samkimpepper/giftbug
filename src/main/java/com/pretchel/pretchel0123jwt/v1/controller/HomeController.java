package com.pretchel.pretchel0123jwt.v1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/payment")
    public String payment() {
        return "payment";
    }
}
