package com.blog.velogclone.repository;

import com.blog.velogclone.entity.ReReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReReplyRepository extends JpaRepository<ReReply, Long> {

    @Query("select m from ReReply m join fetch m.user where m.replyNo = :replyNo and m.reReplyStatus = 'N' order by m.reReplyDate")
    List<ReReply> findByReplyNo(Long replyNo);

    ReReply findByReReplyNo(Long reReplyNo);
}
