package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select m from Post m join fetch m.user where m.postStatus = 'N' ORDER BY m.createDate DESC")
    List<Post> findAllPost();

    @Query("select m from Post m join fetch m.user where m.postNo = :postNo")
    Post findByPostNo(@Param("postNo") int postNo);
}
