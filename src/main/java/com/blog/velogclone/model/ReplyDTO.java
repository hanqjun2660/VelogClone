package com.blog.velogclone.model;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReplyDTO {
    private Long replyNo;
    private String replyBody;
    private Long postNo;
    private Long userNo;
    private Date replyDate;
    private boolean canEdit;
    private UserDTO user;
}
