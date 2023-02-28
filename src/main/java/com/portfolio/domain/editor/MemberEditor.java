package com.portfolio.domain.editor.member;

import com.portfolio.domain.Member;
import com.portfolio.request.member.PasswordChangeRequest;
import com.portfolio.request.member.UnregisterRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.portfolio.repository.util.MemberUtil.*;

@Data
public class MemberEditor {

    private String password;
    private Boolean isEnabled;
    private Boolean isOpened;

    @Builder
    public MemberEditor(String password, Boolean isEnabled, Boolean isOpened) {
        this.password = password;
        this.isEnabled = isEnabled;
        this.isOpened = isOpened;
    }

    public static void editPassword(PasswordChangeRequest request, Member member, PasswordEncoder passwordEncoder) {

        String encodedNewPassword = validatePassword(member, request, passwordEncoder);

        MemberEditor memberEditor = member.toEditor()
                .password(encodedNewPassword)
                .build();

        member.editPassword(memberEditor);
    }

    public static void unregister(UnregisterRequest request, Member member,
                                  PasswordEncoder passwordEncoder) {

        validatePassword(member, request.getPassword(), passwordEncoder);

        MemberEditor memberEditor = member.toEditor()
                .isEnabled(false)
                .build();

        member.inactivateMember(memberEditor);
    }

    public static void close() {

    }
}
