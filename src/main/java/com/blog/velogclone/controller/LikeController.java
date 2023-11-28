package com.blog.velogclone.controller;

import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/add")
    @ResponseBody
    public String likeAdd(@RequestBody LikeDTO likeDTO) {
        log.info("add: {}", likeDTO.getPostNo());

        String response = "";
        LikeDTO responseDTO = likeService.likeAdd(likeDTO);

        if(ObjectUtils.isEmpty(responseDTO)) {
            return response = "fail like add";
        }

        return response = "success like add";
    }
}
