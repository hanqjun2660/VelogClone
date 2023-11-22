package com.blog.velogclone.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Entity
@Table(name = "TBL_RE_REPLY")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Getter
@ToString
@SequenceGenerator(
        name = "RE_REPLY_SEQ"
        , sequenceName = "RE_REPLY_SEQ"
        , initialValue = 1
        , allocationSize = 1
)
public class ReReply {

    @Id
    @Column(name = "RE_REPLY_NO")
    private Long reReplyNo;

    @Column(name = "RE_REPLY_BODY")
    private String reReplyBody;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_NO")
    private User user;

    @Column(name = "REPLY_NO")
    private Long replyNo;

    @Column(name = "RE_REPLY_DATE")
    private Date reReplyDate;

    @Column(name = "RE_REPLY_STATUS")
    private String reReplyStatus;

    @Builder
    public ReReply(Long reReplyNo, String reReplyBody, User user, Long replyNo, Date reReplyDate, String reReplyStatus) {
        this.reReplyNo = reReplyNo;
        this.reReplyBody = reReplyBody;
        this.user = user;
        this.replyNo = replyNo;
        this.reReplyDate = reReplyDate;
        this.reReplyStatus = reReplyStatus;
    }
}
