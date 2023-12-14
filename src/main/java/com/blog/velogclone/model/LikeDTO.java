package com.blog.velogclone.model;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LikeDTO {

    private Long readNo;
    private Post post;
    private User user;
    private Date createDate;
    private Long postNo;
    private Long userNo;

    public boolean isNull() {
        return readNo == null
                && postNo == null
                && userNo == null
                && post == null
                && user == null
                && createDate == null;
    }
}
