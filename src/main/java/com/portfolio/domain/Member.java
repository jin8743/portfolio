package com.portfolio.domain;

import com.portfolio.domain.editor.member.MemberEditor;
import com.portfolio.exception.custom.AuthenticationFailedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.portfolio.domain.MemberRole.*;
import static com.portfolio.domain.Post.*;
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

    //회원 탈퇴 여부
    private Boolean isEnabled;

    //내가 쓴 글, 댓글 공개 여부
    private Boolean isOpened;

    @Builder
    public Member(String username, String password, MemberRole role,
                  Boolean isEnabled, Boolean isOpened) {

        this.username = username;
        this.password = password;
        this.role = role;
        this.isEnabled = isEnabled;
        this.isOpened = isOpened;
    }

    public static void validatePost(Post post, Member member) {
        checkNull(post);
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
    public void editPassword(MemberEditor memberEditor) {
        this.password = memberEditor.getPassword();
    }

    public void inactivateMember(MemberEditor memberEditor) {
        this.isEnabled = memberEditor.getIsEnabled();
    }

    public void changeToPrivate(MemberEditor memberEditor) {
        this.isOpened = memberEditor.getIsPrivate();
    }
    public static Boolean isAdmin(Member member) {
        return member.getRole().equals(ROLE_ADMIN);
    }

}
