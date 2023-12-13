package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "select m from Post m left join m.user where m.postStatus = 'N' ORDER BY m.createDate DESC",
            countQuery = "select count(m) from Post m where m.postStatus = 'N'")
    Page<Post> findAll(Pageable pageable);

    @Query("select m from Post m join fetch m.user where m.postNo = :postNo")
    Post findByPostNo(@Param("postNo") int postNo);

    List<Post> findByUserUserNoAndPostStatus(Long userNo, String postStatus);

    List<Post> findByUserUserNoAndPostTagAndPostStatus(Long userNo, String postTag, String postStatus);
}
