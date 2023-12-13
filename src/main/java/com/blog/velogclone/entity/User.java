package com.blog.velogclone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TBL_USER")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Setter
@Getter
@ToString
@SequenceGenerator(
        name = "USER_SEQ_GENERATOR"
        , sequenceName = "USER_SEQ"
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

    @Column(name = "PROFILE_IMG")
    private String profileImg;

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
    private Long roleNo;

    @Builder
    public User(Long userNo, String userId, String userPw, String userEmail, String userStatus, Date userRegistDate, String provider, String profileImg, String userNickname, String userIntroduce, String userBlogName, String userFacebook, String userGithub, String userTwitter, String userHomepage, Long roleNo) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPw = userPw;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
        this.userRegistDate = userRegistDate;
        this.provider = provider;
        this.profileImg = profileImg;
        this.userNickname = userNickname;
        this.userIntroduce = userIntroduce;
        this.userBlogName = userBlogName;
        this.userFacebook = userFacebook;
        this.userGithub = userGithub;
        this.userTwitter = userTwitter;
        this.userHomepage = userHomepage;
        this.roleNo = roleNo;
    }

    public void updateProfileImagePath(String newPath) {
        this.profileImg = newPath;
    }

    public void updateNickname(String nickName) {
        this.userNickname = nickName;
    }

    public void updateIntroduce(String introduce) {
        this.userIntroduce = introduce;
    }
}
