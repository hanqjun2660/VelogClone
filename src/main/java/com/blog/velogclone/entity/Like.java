package com.blog.velogclone.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "TBL_LIKE")
@Entity
@Builder
@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@ToString
@SequenceGenerator(
        name = "LIKE_SEQ_GENERATOR"
        , sequenceName = "LIKE_SEQ"
        , initialValue = 1
        , allocationSize = 1
)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LIKE_SEQ_GENERATOR")
    private Long readNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="POST_NO")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="USER_NO")
    private User user;

    @Builder
    public Like(Long readNo, Post post, User user) {
        this.readNo = readNo;
        this.post = post;
        this.user = user;
    }
}
