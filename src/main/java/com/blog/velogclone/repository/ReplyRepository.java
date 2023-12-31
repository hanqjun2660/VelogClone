package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select m from Reply m join fetch m.user where m.post.postNo = :postNo and m.replyStatus = 'N' order by m.replyDate")
    List<Reply> findByPostNo(int postNo);

    Reply findByReplyNoAndReplyStatus(Long replyNo, String n);

    String countByPostPostNoAndReplyStatus(Long postNo, String n);
}
