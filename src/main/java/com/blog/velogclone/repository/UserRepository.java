package com.blog.velogclone.repository;

import com.blog.velogclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUserId(String userId);

    @Modifying
    @Query(value = "UPDATE TBL_USER SET PROFILE_IMG = NULL WHERE USER_NO = :userNo", nativeQuery = true)
    int deleteProfileImage(@Param("userNo") Long userNo);

    Optional<User> findByUserBlogNameAndUserStatus(String blogname, String n);
}
