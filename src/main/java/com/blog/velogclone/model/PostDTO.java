package com.blog.velogclone.model;

import com.blog.velogclone.entity.Reply;
import lombok.*;

import java.util.Date;
import java.util.List;

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

    private String srcAttr;

    private UserDTO user;

    private int replyCount;

    private int likeCount;

    private Long postTagCount;

    private List<ReplyDTO> replies;
}
