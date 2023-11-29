package com.blog.velogclone.controller;

import com.blog.velogclone.model.LikeDTO;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    final String DEFAULT_IMG = "/images/clog_text_logo.png";

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
    public String findLikeList(Model model) {
        List<PostDTO> likePostList = likeService.findLikeList();

        try {
            for (PostDTO post : likePostList) {
                String postBody = post.getPostBody();
                Document doc = Jsoup.parse(postBody);
                Elements imgTags = doc.select("img");

                if (!imgTags.isEmpty()) {
                    Element firstImg = imgTags.first();
                    String srcAttribute = firstImg.attr("src");
                    post.setSrcAttr(srcAttribute); // PostDTO에 속성 추가
                } else {
                    post.setSrcAttr(DEFAULT_IMG); // 이미지가 없는 경우 기본 이미지로 설정
                }
            }
        } catch (Exception e) {
            log.info("parsing error");
        }

        model.addAttribute("posts", likePostList);
        model.addAttribute("title", "읽기 목록");
        return "/my/redinglist";
    }
}
