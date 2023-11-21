package com.blog.velogclone.model;

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
}
