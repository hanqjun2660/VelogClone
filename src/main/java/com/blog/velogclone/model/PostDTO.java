package com.blog.velogclone.model;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PostDTO {

    private Long postNo;

    private String postTitle;

    private String postBody;

    private String postTag;

    private Date createDate;

    private String postStatus;

    private int postLike;

    private int postViews;

    private Long userNo;

    private UserDTO user;
}
