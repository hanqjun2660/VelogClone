package com.blog.velogclone.controller;

import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.service.LikeService;
import com.blog.velogclone.service.PostService;
import com.blog.velogclone.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> likeAdd(@RequestBody LikeDTO likeDTO) {
        log.info("add: {}", likeDTO.getPostNo());

        Map<String, Object> response = new HashMap<>();
        LikeDTO responseDTO = likeService.likeAdd(likeDTO);

        if(responseDTO.isNull()) {
            response.put("msg", "fail like add");
            response.put("code", "500");
            return response;
        }

        response.put("msg", "success like add");
        response.put("code", "200");
        return response;
    }

    @PostMapping("/cancle")
    @ResponseBody
    public Map<String, Object> likeCancle(@RequestBody PostDTO postDTO) {
        log.info("add: {}", postDTO.getPostNo());

        Map<String, Object> responseMap = new HashMap<>();
        boolean response = likeService.likeCancle(postDTO);

        if(!response) {
            responseMap.put("msg", "fail like add");
            responseMap.put("code", "500");
            return responseMap;
        }

        responseMap.put("msg", "success like cancle");
        responseMap.put("code", "200");
        return responseMap;
    }

    @PostMapping("/checklike")
    @ResponseBody
    public Map<String, Object> checkLike(@RequestBody LikeDTO likeDTO) {
        log.info("add: {}", likeDTO.getPostNo());
        Map<String, Object> response = new HashMap<>();
        response.put("status", likeService.checkLike(likeDTO));
        return response;
    }

    @GetMapping("/list")
    public String readingListPage() {
        return "/my/readingList";
    }

    @GetMapping("/findlist")
    @ResponseBody
    public List<PostDTO> getUserLikePostList(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 8;

        List<PostDTO> likePostList = likeService.findByUserUserNoAndPostStatus(page, pageSize);

        return likePostList;
    }
}
