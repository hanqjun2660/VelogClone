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
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Getter
@ToString
@Table(name = "TBL_REPLY")
@SequenceGenerator(
        name = "REPLY_SEQ_GENERATOR"
        , sequenceName = "REPLY_SEQ"
        , initialValue = 1
        , allocationSize = 1
)
public class Reply {

    @Id
    @Column(name = "REPLY_NO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLY_SEQ_GENERATOR")
    private Long replyNo;

    @Column(name = "REPLY_BODY")
    private String replyBody;

    @Column(name = "REPLY_DATE")
    private Date replyDate;

    @Column(name = "REPLY_STATUS")
    private String replyStatus;

    @ManyToOne
    @JoinColumn(name="USER_NO")
    private User user;

    @ManyToOne
    @JoinColumn(name = "POST_NO")
    private Post post;

    @Builder
    public Reply(Long replyNo, String replyBody, Post post, Date replyDate, String replyStatus, User user) {
        this.replyNo = replyNo;
        this.replyBody = replyBody;
        this.post = post;
        this.replyDate = replyDate;
        this.replyStatus = replyStatus;
        this.user = user;
    }
}
