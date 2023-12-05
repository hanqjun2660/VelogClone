package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findAllByUser_UserNo(Long userNo);

    Like findByUser_UserNoAndPost_PostNo(Long userNo, Long postNo);

    void deleteByReadNo(Long readNo);

    @Query("select m from Like m join fetch m.user join fetch m.post where m.post.postStatus = 'N' and m.user.userNo = :userNo ORDER BY m.createDate DESC")
    List<Like> findByUserNo(Long userNo);
}
