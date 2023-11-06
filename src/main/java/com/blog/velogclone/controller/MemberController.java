package com.blog.velogclone.controller;

import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/regist")
    public String regist(Model model) {
        model.addAttribute("title", "회원가입");
        return "/user/regist";
    }

    @PostMapping("/regist")
    @ResponseBody
    public Map<String, String> registUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        int result = memberService.registUser(userDTO);

        if(result >= 1) {
            response.put("msg", "회원가입에 성공하였습니다.");
        } else {
            response.put("msg", "회원가입에 실패하였습니다.");
        }

        return response;
    }

}
