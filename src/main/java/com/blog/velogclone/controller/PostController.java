package com.blog.velogclone.controller;

import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/dashboard")
    public String dashbaord(Model model) {
        List<PostDTO> postList = postService.findAll();
        model.addAttribute("posts", postList);
        return "/dashboard";
    }
}
