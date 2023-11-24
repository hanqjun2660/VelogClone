package com.blog.velogclone.service;

import com.blog.velogclone.entity.ReReply;
import com.blog.velogclone.entity.User;
import com.blog.velogclone.model.ReReplyDTO;
import com.blog.velogclone.repository.ReReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReReplyService {

    private final ReReplyRepository reReplyRepository;
    private final ModelMapper modelMapper;

    public List<ReReply> findByReplyNo(Long replyNo) {
        List<ReReply> reReplyList = reReplyRepository.findByReplyNo(replyNo);
        return reReplyList.isEmpty() ? Collections.emptyList() : reReplyList;
    }

    public ReReplyDTO save(ReReplyDTO reReplyDTO) {
        ReReply request = ReReply.builder()
                .replyNo(reReplyDTO.getReplyNo())
                .reReplyBody(reReplyDTO.getReReplyBody())
                .user(User.builder().userNo(reReplyDTO.getUserNo()).build()).build();

        ReReply responseEntity = reReplyRepository.save(request);

        return modelMapper.map(responseEntity, ReReplyDTO.class);
    }

    public ReReplyDTO modifyReReply(ReReplyDTO reReplyDTO) {
        ReReply selectEntity = reReplyRepository.findByReReplyNo(reReplyDTO.getReReplyNo());

        if(ObjectUtils.isEmpty(selectEntity)) {
            return new ReReplyDTO();
        }

        ReReply request = ReReply.builder()
                .replyNo(selectEntity.getReplyNo())
                .reReplyBody(reReplyDTO.getReReplyBody())
                .user(User.builder().userNo(selectEntity.getUser().getUserNo()).build())
                .reReplyNo(reReplyDTO.getReReplyNo())
                .reReplyDate(selectEntity.getReReplyDate())
                .reReplyStatus(selectEntity.getReReplyStatus())
                .build();

        ReReply responseEntity = reReplyRepository.save(request);

        return modelMapper.map(responseEntity, ReReplyDTO.class);
    }
}
