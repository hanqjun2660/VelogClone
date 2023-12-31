package com.blog.velogclone.model;

import com.blog.velogclone.entity.Reply;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private Long userNo;
    private String userId;
    private String userPw;
    private String userNickname;
    private String profileImg;
    private String userEmail;
    private String userBlogName;
    private String userIntroduce;
    private String userGithub;
    private String userFacebook;
    private String userHomepage;
    private String userTwitter;
}
