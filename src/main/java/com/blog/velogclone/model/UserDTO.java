package com.blog.velogclone.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {

    private String userId;
    private String userPw;
    private String userNickname;
    private String profileImg;
    private String userEmail;
    private String userBlogName;
    private String userIntroduce;
}
