package com.blog.velogclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

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

    @Column(name="POST_VIEWS")
    private int postViews;

    @Column(name="USER_NO")
    private Long userNo;
}
