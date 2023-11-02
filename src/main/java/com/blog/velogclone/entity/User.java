package com.blog.velogclone.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

@Entity(name = "User")
@Table(name = "TBL_USER")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Getter
@SequenceGenerator(
        name = "USER_SEQ_GENERATOR"
        , sequenceName = "USER_SEQ_GENERATOR"
        , initialValue = 1
        , allocationSize = 1
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ_GENERATOR")
    @Column(name = "USER_NO")
    private Long userNo;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_PW")
    private String userPw;

    @Column(name = "USER_EMAIL")
    private String userEmail;

    @Column(name = "USER_STATUS")
    private String userStatus;

    @Column(name = "USER_REGIST_DATE")
    private Date userRegistDate;

    @Column(name = "PROVIDER")
    private String provider;

    @Column(name = "USER_NICKNAME")
    private String userNickname;

    @Column(name = "USER_INTRODUCE")
    private String userIntroduce;

    @Column(name = "USER_BLOG_NAME")
    private String userBlogName;

    @Column(name = "USER_FACEBOOK")
    private String userFacebook;

    @Column(name = "USER_GITHUB")
    private String userGithub;

    @Column(name = "USER_TWITTER")
    private String userTwitter;

    @Column(name = "USER_HOMEPAGE")
    private String userHomepage;

    @Column(name = "ROLE_NO")
    private String roleNo;

    @Builder
    public User(String userId, String userPw, String userEmail, String userNickname, String userIntroduce, String userBlogName, String userFacebook, String userGithub, String userTwitter, String userHomepage) {
        this.userId = userId;
        this.userPw = userPw;
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userIntroduce = userIntroduce;
        this.userBlogName = userBlogName;
        this.userFacebook = userFacebook;
        this.userGithub = userGithub;
        this.userTwitter = userTwitter;
        this.userHomepage = userHomepage;
    }
}
