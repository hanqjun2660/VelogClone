package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUser_UserNo(Long userNo);

    Like findByUser_UserNoAndPost_PostNo(Long userNo, Long postNo);

    void deleteByReadNo(Long readNo);

    String countByPostPostNo(Long postNo);

    List<Like> findByUserUserNo(Long userNo);
}
