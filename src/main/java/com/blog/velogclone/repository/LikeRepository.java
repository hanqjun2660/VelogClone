package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUser_UserNo(Long userNo);
}
