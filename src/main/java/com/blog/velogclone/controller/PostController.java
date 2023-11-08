package com.blog.velogclone.controller;

import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/post/{postNo}")
    public String postDetail(@PathVariable int postNo, Model model) {

        PostDTO postDTO = postService.findByPostNo(postNo);

        model.addAttribute("postdetail", postDTO);
        model.addAttribute("title", postDTO.getPostTitle());

        return "/post/detail";
    }
}
