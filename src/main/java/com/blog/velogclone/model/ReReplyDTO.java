package com.blog.velogclone.model;

import com.blog.velogclone.entity.Post;
import com.blog.velogclone.entity.User;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReReplyDTO {

    private Long reReplyNo;
    private String reReplyBody;
    private Long userNo;
    private Long replyNo;
    private Date reReplyDate;
    private String reReplyStatus;
    private User user;
    private Post post;
    private boolean canEdit;
}
