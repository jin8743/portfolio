package com.portfolio.domain.editor;

import com.portfolio.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEditor {

    private String password;

    @Builder
    public MemberEditor(String password) {
        this.password = password;
    }

    public static void editPassword(Member member, String encodedNewPassword) {

        MemberEditor memberEditor = member.toEditor()
                .password(encodedNewPassword)
                .build();

        member.editPassword(memberEditor);
    }
}
