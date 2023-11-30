package com.blog.velogclone.controller;

import com.blog.velogclone.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/setting")
    public String settingUser() {
        return "/my/setting";
    }
}
