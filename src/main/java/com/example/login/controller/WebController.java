package com.example.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    @GetMapping({"/", "/login"})
    public String login(HttpSession session) {
        return "login";
    }

//    @SaCheckLogin
    @GetMapping("/main")
    public String toMain() {
        return "main";
    }

    @GetMapping("/review")
    public String toReview() {
        return "review";
    }

    @GetMapping("/slides")
    public String toSlides() {
        return "slides";
    }

    @GetMapping("/settings")
    public String setting() {
        return "setting";
    }
}
