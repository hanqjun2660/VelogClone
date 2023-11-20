package com.blog.velogclone.controller;

import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.ReplyDTO;
import com.blog.velogclone.service.ReplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/write")
    @ResponseBody
    public Map<String, String> writeReply(@RequestBody ReplyDTO replyDTO) {

        log.info(replyDTO.toString());

        Map<String, String> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            Long userNo = ((PrincipalDetails) authentication.getPrincipal()).getUserNo();
            log.info("userNo : {}", userNo);

            replyDTO.setUserNo(userNo);

            ReplyDTO responseDTO = replyService.saveReply(replyDTO);

            if(!ObjectUtils.isEmpty(responseDTO)) {
                response.put("msg", "댓글이 성공적으로 등록되었습니다.");
            } else {
                response.put("msg", "댓글 등록 실패");
            }
        } else {
            response.put("status", "N");
            response.put("msg", "댓글을 작성하려면 로그인해주세요.");
        }

        return response;
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, String> deleteReply(@RequestBody ReplyDTO replyDTO) {
        log.info(replyDTO.toString());

        Map<String, String> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            Long userNo = ((PrincipalDetails) authentication.getPrincipal()).getUserNo();
            log.info("userNo : {}", userNo);

            replyDTO.setUserNo(userNo);

            ReplyDTO responseDTO = replyService.deleteReply(replyDTO);

            if(!ObjectUtils.isEmpty(responseDTO)) {
                response.put("msg", "댓글이 성공적으로 삭제되었습니다.");
            } else {
                response.put("msg", "댓글 삭제 실패");
            }
        }

        return response;

    }
}
