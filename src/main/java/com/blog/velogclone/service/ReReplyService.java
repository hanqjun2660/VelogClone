package com.blog.velogclone.service;

import com.blog.velogclone.entity.ReReply;
import com.blog.velogclone.repository.ReReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReReplyService {

    private final ReReplyRepository reReplyRepository;

    public List<ReReply> findByReplyNo(Long replyNo) {
        List<ReReply> reReplyList = reReplyRepository.findByReplyNo(replyNo);
        return reReplyList.isEmpty() ? Collections.emptyList() : reReplyList;
    }
}
