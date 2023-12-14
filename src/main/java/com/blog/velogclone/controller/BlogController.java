package com.blog.velogclone.controller;

import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/blog")
public class BlogController {

    private final MemberService memberService;

    @GetMapping("/{blogname}")
    public String selectMyBlog(@PathVariable String blogname, Model model) {
        log.info("MyBlogController : {}", blogname);

        try {
            UserDTO userDTO = memberService.selectUser(blogname);
            model.addAttribute("userData", userDTO);

            model.addAttribute("title", blogname + ".log");
        } catch (EntityNotFoundException e) {
            log.info("User Not Found : { }", e);
        }

        return "/blog/blog";
    }
}
