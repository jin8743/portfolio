package com.portfolio.domain;

import com.portfolio.exception.custom.AuthenticationFailedException;
import jdk.jfr.Unsigned;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;


    @Builder
    public Member(String username, String password, MemberRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static Post validatePost(Post post, Member member) {
        if (post.getMember() == member) {
            return post;
        } else {
            throw new AuthenticationFailedException();
        }
    }

    public static Comment validateComment(Comment comment, Member member) {
        if (comment.getMember() == member) {
            return comment;
        } else {
            throw new AuthenticationFailedException();
        }
    }
}
