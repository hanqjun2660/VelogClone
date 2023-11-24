package com.blog.velogclone.controller;

import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.ReReplyDTO;
import com.blog.velogclone.service.ReReplyService;
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
@Slf4j
@RequestMapping("/rereply")
@AllArgsConstructor
public class ReReplyController {

    private final ReReplyService reReplyService;
    @PostMapping("/write")
    @ResponseBody
    public Map<String, Object> registReReply(@RequestBody ReReplyDTO reReplyDTO, Authentication authentication) {
        log.info(reReplyDTO.toString());

        Map<String, Object> response = new HashMap<>();

        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof PrincipalDetails)) {
            response.put("msg", "로그인이 필요합니다.");
            response.put("status", "N");
            return response;
        }

        reReplyDTO.setUserNo(((PrincipalDetails) authentication.getPrincipal()).getUserNo());

        ReReplyDTO responseDTO = reReplyService.save(reReplyDTO);

        if(ObjectUtils.isEmpty(responseDTO)) {
            response.put("msg", "답글 작성에 실패하였습니다.");
            response.put("status", "N");
        }

        response.put("msg", "답글 작성에 성공하였습니다.");
        response.put("status", "Y");

        return response;
    }

    @PostMapping("/modify")
    @ResponseBody
    public Map<String, Object> modifiyReReply(@RequestBody ReReplyDTO reReplyDTO) {
        log.info(reReplyDTO.toString());
        Map<String, Object> response = new HashMap<>();

        ReReplyDTO responseDTO = reReplyService.modifyReReply(reReplyDTO);

        if(ObjectUtils.isEmpty(responseDTO)) {
            response.put("msg", "수정에 실패하였습니다.");
            response.put("status", "N");
        }

        response.put("msg", "수정에 성공하였습니다.");
        response.put("status", "Y");

        return response;
    }
}
