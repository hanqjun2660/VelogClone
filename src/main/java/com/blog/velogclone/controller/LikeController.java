package com.blog.velogclone.controller;

import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
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
}
