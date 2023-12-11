package com.blog.velogclone.service;

import com.blog.velogclone.entity.ReReply;
import com.blog.velogclone.entity.Reply;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.PrincipalDetails;
import com.blog.velogclone.model.ReReplyDTO;
import com.blog.velogclone.model.ReplyDTO;
import com.blog.velogclone.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final ReReplyService reReplyService;

    private final ModelMapper modelMapper;

    public List<ReplyDTO> findByPostNo(int postNo) {

        List<Reply> replyList = replyRepository.findByPostNo(postNo);

        if(replyList.isEmpty()) {
            log.info("댓글이 존재하지 않습니다.");
            return Collections.emptyList();
        }

        List<ReplyDTO> replyDTOList = new ArrayList<>();
        replyList.forEach(reply -> {
            ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class);
            replyDTOList.add(replyDTO);
        });

        for(ReplyDTO reply : replyDTOList) {
            List<ReReply> reReplyList = reReplyService.findByReplyNo(reply.getReplyNo());
            List<ReReplyDTO> reReplyDTOList = new ArrayList<>();

            reReplyList.forEach(reReply -> {
                ReReplyDTO reReplyDTO = modelMapper.map(reReply, ReReplyDTO.class);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                Long userNo;

                if(authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
                    userNo = ((PrincipalDetails) authentication.getPrincipal()).getUserNo();

                    if(reReplyDTO.getUser().getUserNo().compareTo(userNo) == 0) {
                        reReplyDTO.setCanEdit(true);
                    } else {
                        reReplyDTO.setCanEdit(false);
                    }
                }

                reReplyDTOList.add(reReplyDTO);
            });

            reply.setReplyDTOList(reReplyDTOList);
        }

        return replyDTOList;
    }

    public ReplyDTO saveReply(ReplyDTO replyDTO) {
        Reply request = Reply.builder()
                .replyBody(replyDTO.getReplyBody())
                .postNo(replyDTO.getPostNo())
                .user(User.builder().userNo(replyDTO.getUserNo()).build())
                .build();

        Reply response = replyRepository.save(request);

        return modelMapper.map(response, ReplyDTO.class);
    }

    public ReplyDTO deleteReply(ReplyDTO replyDTO) {

        Reply response = null;

        Reply replyEntity = replyRepository.findByReplyNoAndReplyStatus(replyDTO.getReplyNo(), "N");

        if(!ObjectUtils.isEmpty(replyEntity)) {
            Reply request = Reply.builder()
                    .replyNo(replyEntity.getReplyNo())
                    .replyBody(replyEntity.getReplyBody())
                    .replyDate(replyEntity.getReplyDate())
                    .replyStatus("Y")
                    .postNo(replyEntity.getPostNo())
                    .user(replyEntity.getUser())
                    .build();

            response = replyRepository.save(request);
            log.info(response.toString());
        }

        return response != null ? modelMapper.map(response, ReplyDTO.class) : new ReplyDTO();
    }

    public ReplyDTO modifyReply(ReplyDTO replyDTO) {

        Reply response = null;

        Reply replyEntity = replyRepository.findByReplyNoAndReplyStatus(replyDTO.getReplyNo(), "N");

        if(!ObjectUtils.isEmpty(replyEntity)) {
            Reply request = Reply.builder()
                    .replyNo(replyDTO.getReplyNo())
                    .replyBody(replyDTO.getReplyBody())
                    .replyDate(replyEntity.getReplyDate())
                    .replyStatus(replyEntity.getReplyStatus())
                    .postNo(replyEntity.getPostNo())
                    .user(replyEntity.getUser())
                    .build();

            response = replyRepository.save(request);
            log.info(response.toString());
        }

        return response != null ? modelMapper.map(response, ReplyDTO.class) : new ReplyDTO();
    }

    public int countReply(Long postNo) {
        return Integer.parseInt(replyRepository.countByPostNoAndReplyStatus(postNo, "N"));
    }
}
