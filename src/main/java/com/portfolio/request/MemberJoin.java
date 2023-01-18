package com.portfolio.request;

import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.portfolio.domain.MemberRole.*;

@Data
@NoArgsConstructor
public class MemberJoin {

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 8, max = 20, message = "아이디는 8글자 이상 20글자 이상으로 입력해주세요")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 10, max = 20, message = "비밀번호는 10글자 이상 20글자 이상으로 입력해주세요")
    private String password;


    @Builder
    public MemberJoin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Member toMember(MemberJoin dto, PasswordEncoder encoder) {

        MemberRole memberRole = ROLE_MEMBER;

        //youngjin 이라는 usename을 가지는  Admin 계정 1개 생성
        if (dto.username.equals("youngjin")) {
            memberRole = ROLE_ADMIN;
        }

        return Member.builder()
                .username(dto.username)
                .password(encoder.encode(dto.password))
                .role(memberRole)
                .build();
    }
}
