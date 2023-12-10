package com.blog.velogclone.controller;

import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.UserDTO;
import com.blog.velogclone.service.MemberService;
import com.blog.velogclone.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/blog")
public class MyBlogController {

    private final MemberService memberService;
    private final PostService postService;

    @GetMapping("/{blogname}")
    public String selectMyBlog(@PathVariable String blogname, Model model) {
        log.info("MyBlogController : {}", blogname);

        try {
            UserDTO userDTO = memberService.selectUser(blogname);
            model.addAttribute("userData", userDTO);
        } catch (EntityNotFoundException e) {
            log.info("User Not Found : { }", e);
        }

        return "/my/myblog";
    }

    @PostMapping("/selectuserpost")
    @ResponseBody
    public List<PostDTO> selectUserPost(@RequestBody Map<String, String> requestMap) {
        log.info("BlogController: {}", requestMap.get("blogName"));

        Long userNo = memberService.findUserNo(requestMap.get("blogName"));

        if(!(userNo > 0)) {
            log.info("userNo is Empty");
        }

        log.info("userNo is Not Empty");
        List<PostDTO> findPost = postService.selectUserPost(userNo);

        if(findPost.isEmpty()) {
            log.info("findPost is Empty");
            return Collections.emptyList();
        }

        return findPost;
    }
}
