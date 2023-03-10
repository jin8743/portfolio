package com.portfolio.domain;

import com.portfolio.domain.editor.MemberEditor;
import com.portfolio.exception.custom.AuthenticationFailedException;
import com.portfolio.exception.custom.AuthorizationFailedException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static com.portfolio.domain.MemberRole.*;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE member SET is_enabled = false WHERE member_id=?")
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role = ROLE_MEMBER;

    private Boolean isEnabled = true;


    @Builder
    public Member(String username, String email, String password, MemberRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role == null ? ROLE_MEMBER : role;
    }

    public MemberEditor.MemberEditorBuilder toEditor() {
        return MemberEditor.builder()
                .password(password);
    }


    /** 여기서만 데이터 수정 가능 */
    public void editPassword(MemberEditor memberEditor) {
        this.password = memberEditor.getPassword();
    }
}
