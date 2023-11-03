package com.blog.velogclone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String login() {
        return "/login";
    }

    @GetMapping("/dashboard")
    public void dashbaord() {}
}
