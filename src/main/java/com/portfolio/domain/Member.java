package com.portfolio.domain;

import com.portfolio.domain.util.MemberEditor;
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

import static com.portfolio.domain.MemberRole.*;
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

    public static void validatePost(Post post, Member member) {
        if (post.getMember() != member) {
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

    public MemberEditor.MemberEditorBuilder toEditor() {
        return MemberEditor.builder()
                .password(password);
    }

    /** 여기서만 데이터 수정 가능 */
    public void edit(MemberEditor memberEditor) {
        this.password = memberEditor.getPassword();
    }

    public static Boolean isAdmin(Member member) {
        return member.getRole().equals(ROLE_ADMIN);
    }

}
