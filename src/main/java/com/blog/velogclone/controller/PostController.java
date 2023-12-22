package com.blog.velogclone.controller;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.model.PostDTO;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.ReReplyDTO;
import com.blog.velogclone.model.ReplyDTO;
import com.blog.velogclone.service.LikeService;
import com.blog.velogclone.service.MemberService;
import com.blog.velogclone.service.PostService;
import com.blog.velogclone.service.ReplyService;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@Slf4j
@AllArgsConstructor
public class PostController {

    final String DEFAULT_IMG = "/images/clog_text_logo.png";

    private final PostService postService;
    private final ReplyService replyService;
    private final MemberService memberService;
    private final LikeService likeService;

    @GetMapping("/dashboard")
    public String dashbaord() {
        return "dashboard";
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

                int likeCount = countLike(post.getPostNo());
                log.info("like count: {}", likeCount);
                post.setLikeCount(likeCount);
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

        return "post/detail";
    }

    @GetMapping("/post/write")
    public String postWrite(Model model) {
        model.addAttribute("title", "새 글 작성");
        return "post/write";
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
        return "post/write";
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

    @PostMapping("/post/selectuserpost")
    @ResponseBody
    public Map<String, Object> selectUserPost(@RequestBody Map<String, String> requestMap) {
        log.info("BlogController: {}", requestMap.get("blogName"));

        Long userNo = memberService.findUserNo(requestMap.get("blogName"));

        if(!(userNo > 0)) {
            log.info("userNo is Empty");
        }

        log.info("userNo is Not Empty");
        List<PostDTO> findPost = postService.selectUserPost(userNo);

        Map<String, Object> response = new HashMap<>();
        Map<String, Long> tagCountMap = new HashMap<>();

        if(findPost.isEmpty()) {
            log.info("findPost is Empty");
            response.put("list", Collections.emptyList());
        } else {
            for(PostDTO post : findPost) {
                List<String> tags = new ArrayList<>();
                tags.add(post.getPostTag());

                for(String tag : tags) {
                    tagCountMap.merge(tag, 1L, Long::sum);
                }

                int replyCount = countReply(post.getPostNo());
                log.info("reply count: {}", replyCount);
                post.setReplyCount(replyCount);

                int likeCount = countLike(post.getPostNo());
                log.info("like count: {}", likeCount);
                post.setLikeCount(likeCount);
            }

            Map<String, Long> sortedMap = tagCountMap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> { throw new AssertionError(); },
                                    LinkedHashMap::new
                            ));

            response.put("list", findPost);
            response.put("tagCount", sortedMap);
        }

        return response;
    }

    @PostMapping("/post/blog/list")
    @ResponseBody
    public Map<String, Object> selectUserPostAndTag(@RequestBody Map<String, String> request) {
        log.info("postTag data : {}", request.get("postTag"));
        log.info("userNo data : {}", request.get("userNo"));

        Map<String, Object> findPostMap = postService.findByUserUserNoAndPostTag(Long.parseLong(request.get("userNo")), request.get("postTag"));

        return findPostMap;
    }

    @GetMapping("/search")
    public String search() {
        return "post/search";
    }

    @GetMapping("/search/list")
    @ResponseBody
    public List<PostDTO> getSearchList(@RequestParam(defaultValue = "0") int page, @RequestParam String keyword) {
        int pageSize = 4;
        log.info("searchList Request Page : {}", page);
        log.info("searchList Request Keyword : {}", keyword);

        if(!keyword.isEmpty()) {
            List<PostDTO> findList = postService.findByPostTitleAndPostStatus(keyword, page, pageSize);
            log.info("searchList Response findList : {}", findList);
            return findList;
        }

        return Collections.emptyList();
    }

    public int countReply(Long postNo) {
        return replyService.countReply(postNo);
    }

    public int countLike(Long postNo) {
        return likeService.countLike(postNo);
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
