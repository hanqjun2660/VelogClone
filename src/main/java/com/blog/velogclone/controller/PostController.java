package com.blog.velogclone.controller;

import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.ReReplyDTO;
import com.blog.velogclone.model.ReplyDTO;
import com.blog.velogclone.service.LikeService;
import com.blog.velogclone.service.PostService;
import com.blog.velogclone.service.ReplyService;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@AllArgsConstructor
public class PostController {

    final String DEFAULT_IMG = "/images/clog_text_logo.png";

    private final PostService postService;
    private final ReplyService replyService;

    @GetMapping("/dashboard")
    public String dashbaord(Model model) {
        return "/dashboard";
    }

    @GetMapping("/post/list")
    @ResponseBody
    public List<PostDTO> postList(@RequestParam(defaultValue = "0") int page) {
        int pageSize = 8;
        List<PostDTO> postList = postService.findAll(page, pageSize);

        try {
            for (PostDTO post : postList) {
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

        return postList;
    }


    @GetMapping("/post/{postNo}")
    public String postDetail(@PathVariable int postNo, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userNo = null;

        PostDTO postDTO = postService.findByPostNo(postNo);
        List<ReplyDTO> replyList = replyService.findByPostNo(postNo);
        int replyCount = replyList.size();

        log.info(postDTO.toString());
        log.info(replyList.toString());

        if(authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            userNo = ((PrincipalDetails) authentication.getPrincipal()).getUserNo();
            log.info("userNo : {}", userNo);

            if(userNo.compareTo(postDTO.getUser().getUserNo()) == 0) {
                model.addAttribute("isAuthorization", true);
            } else {
                model.addAttribute("isAuthorization", false);
            }
        }

        for(ReplyDTO replyDTO : replyList) {
            if(userNo != null && userNo.equals(replyDTO.getUser().getUserNo())) {
                replyDTO.setCanEdit(true);
            } else {
                replyDTO.setCanEdit(false);
            }
        }

        for(ReplyDTO replyDTO : replyList) {
            List<ReReplyDTO> reReplyList = replyDTO.getReplyDTOList();
            int reReplyCount = reReplyList.size();
            replyDTO.setReReplyCount(reReplyCount);
        }

        model.addAttribute("postdetail", postDTO);
        model.addAttribute("replyList", replyList);
        model.addAttribute("replycount", replyCount);
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

    @PostMapping("/post/delete")
    @ResponseBody
    public Map<String, String> deletePost(@RequestBody PostDTO postDTO) {
        log.info(postDTO.toString());

        Map<String, String> response = new HashMap<>();
        PostDTO result = postService.deletePost(postDTO);

        if(!ObjectUtils.isEmpty(result)) {
            response.put("code", "200");
            response.put("msg", "게시글이 삭제되었습니다.");
        } else {
            response.put("code", "400");
            response.put("msg", "게시글 삭제에 실패하였습니다.");
        }

        return response;
    }

    @GetMapping("/post/modify/{id}")
    public String modifyPost(@PathVariable Long id, Model model) {
        model.addAttribute("title", "게시글 수정");
        return "/post/write";
    }

    @PostMapping("/post/checkdata")
    @ResponseBody
    public Map<String, Object> checkData(@RequestBody Map<String, Object> param) {
        Map<String, Object> response = new HashMap<>();

        PostDTO postDTO = postService.findByPostNo((Integer) param.get("postId"));
        String parsingContents = convertHtmlToMarkdown(postDTO.getPostBody());
        log.info(parsingContents);

        response.put("checkData", parsingContents);
        response.put("postTitle", postDTO.getPostTitle());
        response.put("postTag", postDTO.getPostTag());
        response.put("postNo", postDTO.getPostNo());

        return response;
    }

    /**
     * HTML -> MD paser
     * @param htmlData
     * @return MDData
     */
    private String convertHtmlToMarkdown(String htmlData) {
        return FlexmarkHtmlConverter.builder().build().convert(htmlData);
    }

}
