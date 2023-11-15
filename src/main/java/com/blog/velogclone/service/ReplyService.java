package com.blog.velogclone.service;

import com.blog.velogclone.entity.Reply;
import com.blog.velogclone.model.ReplyDTO;
import com.blog.velogclone.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;

    private final ModelMapper modelMapper;

    public List<ReplyDTO> findByPostNo(int postNo) {
        List<Reply> replyList = replyRepository.findByPostNo(postNo);
        if(replyList.isEmpty()) {
            log.info("댓글이 존재하지 않습니다.");
            return Collections.emptyList();
        }
        return replyList.stream().map(reply -> modelMapper.map(reply, ReplyDTO.class)).collect(Collectors.toList());
    }

}
