package com.blog.velogclone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
public class MyBlogController {
    @GetMapping("/{blogname}")
    public String selectMyBlog(@PathVariable String blogname) {
        log.info("MyBlogController : {}", blogname);
        return "/my/myblog";
    }
}
