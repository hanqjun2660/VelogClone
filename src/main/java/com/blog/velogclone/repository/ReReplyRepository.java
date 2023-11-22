package com.blog.velogclone.repository;

import com.blog.velogclone.entity.ReReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReReplyRepository extends JpaRepository<ReReply, Long> {
    List<ReReply> findByReplyNo(Long replyNo);
}
