package com.portfolio.request.member;

import com.portfolio.domain.Member;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class SignUp {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp="[a-zA-Z0-9]{3,20}",
            message = "아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요.")
    private String username;

    @Email(message = "이메일 형식으로 입력해주세요")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$",
            message = "비밀번호는 영어, 숫자, 특수문자를 포함해서 8~20자리 이내로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호를 한번 더 입력해주세요")
    private String passwordConfirm;

    @Builder
    public SignUp(String username, String email, String password, String passwordConfirm) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public static Member createNewMember(SignUp request) {
        return Member.builder()
                .username(request.username)
                .email(request.getEmail())
                .password(request.password)
                .build();
    }
}
