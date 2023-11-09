package com.blog.velogclone.controller;

import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
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

    @GetMapping("/post/write")
    public String postWrite(Model model) {
        model.addAttribute("title", "새 글 작성");
        return "/post/write";
    }

    @PostMapping("/post/write")
    @ResponseBody
    public Map<String, String> savePost(@RequestBody PostDTO postDTO) {

        Map<String, String> response = new HashMap<>();

        if(postDTO.getPostTitle() == null || postDTO.getPostTitle().isEmpty() || postDTO.getPostBody() == null || postDTO.getPostBody().isEmpty()) {
            log.info("제목 또는 본문이 없음");
            response.put("msg", "제목과 본문을 모두 입력해주세요");
            response.put("status", "fail");
            return response;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo;

        if(authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            userNo = ((PrincipalDetails) authentication.getPrincipal()).getUserNo();
            log.info("userNo : {}", userNo);
            postDTO.setUserNo(userNo);
        }

        PostDTO result = postService.savePost(postDTO);


        if(!ObjectUtils.isEmpty(result)) {
            response.put("msg", "게시물 작성에 성공하였습니다.");
        } else {
            response.put("msg", "게시물 작성에 실패하였습니다.");
        }

        return response;
    }

}
