package com.blog.velogclone.repository;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.model.PostDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select m from Post m join fetch m.user ORDER BY m.postNo DESC")
    List<Post> findAllPost();

    @Query("select m from Post m join fetch m.user where m.postNo = :postNo")
    Post findByPostNo(@Param("postNo") int postNo);
}
