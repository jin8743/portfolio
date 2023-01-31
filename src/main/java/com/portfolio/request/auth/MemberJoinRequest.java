package com.portfolio.request.auth;

import com.portfolio.domain.Member;
import com.portfolio.domain.MemberRole;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.portfolio.domain.MemberRole.*;

@Data
@NoArgsConstructor
public class MemberJoinRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp="[a-zA-Z1-9]{3,20}",
            message = "아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp="[a-zA-Z1-9]{8,20}",
            message = "비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요.")
    private String password;


    @Builder
    public MemberJoinRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Member toMember(MemberJoinRequest dto, PasswordEncoder encoder) {

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
