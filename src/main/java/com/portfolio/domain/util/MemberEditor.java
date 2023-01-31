package com.portfolio.domain.util;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.InvalidPasswordChangeRequestException;
import com.portfolio.request.auth.MemberEditRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class MemberEditor {
    private String password;

    @Builder
    public MemberEditor(String password) {
        this.password = password;
    }


    public static void editMember(MemberEditRequest memberEditRequest, Member member, PasswordEncoder passwordEncoder) {

        if (passwordEncoder.matches(memberEditRequest.getPassword(), member.getPassword())) {
            throw new InvalidPasswordChangeRequestException();
        }

        String newPassword = passwordEncoder.encode(memberEditRequest.getPassword());

        MemberEditor memberEditor = member.toEditor()
                .password(newPassword)
                .build();

        member.edit(memberEditor);
    }
}
