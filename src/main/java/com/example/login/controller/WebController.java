package com.example.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/login"})
    public String login() {

        return "login";
    }

    @GetMapping("/main")
    public String toMain() {
        return "main";
    }

}
