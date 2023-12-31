package com.blog.velogclone.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TBL_POST")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Getter
@ToString
@SequenceGenerator(
        name = "POST_SEQ_GENERATOR"
        , sequenceName = "POST_SEQ"
        , initialValue = 1
        , allocationSize = 1
)
public class Post {

    @Id
    @Column(name = "POST_NO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_SEQ_GENERATOR")
    private Long postNo;

    @Column(name="POST_TITLE")
    private String postTitle;

    @Column(name="POST_BODY")
    private String postBody;

    @Column(name="POST_TAG")
    private String postTag;

    @Column(name="CREATE_DATE")
    private Date createDate;

    @Column(name="POST_STATUS")
    private String postStatus;

    @Column(name="POST_LIKE")
    private int postLike;

    @Column(name="POST_VIEW")
    private int postView;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="USER_NO")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Reply> replies;

    @Builder
    public Post(Long postNo, String postTitle, String postBody, String postTag, Date createDate, String postStatus, int postLike, int postView, User user) {
        this.postNo = postNo;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postTag = postTag;
        this.createDate = createDate;
        this.postStatus = postStatus;
        this.postLike = postLike;
        this.postView = postView;
        this.user = user;
    }
}
