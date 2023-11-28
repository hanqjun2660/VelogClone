package com.blog.velogclone.model;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LikeDTO {

    private Long likeNo;
    private Long postNo;
    private Long userNo;
    private Post post;
    private User user;
}
