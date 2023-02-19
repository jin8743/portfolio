package com.portfolio.request.member;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.InvalidPasswordException;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.portfolio.domain.MemberRole.*;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp="[a-zA-Z1-9]{3,20}",
            message = "아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp="[a-zA-Z1-9]{8,20}",
            message = "비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요.")
    private String password;

    private String confirmPassword;

    @Builder
    public JoinRequest(String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public static Member toMember(JoinRequest memberJoin, PasswordEncoder encoder) {
        return Member.builder()
                .username(memberJoin.username)
                .password(encoder.encode(memberJoin.password))
                .isEnabled(true)
                .isPublic(true)
                .role(ROLE_MEMBER)
                .build();
    }


    /** 비밀번호와 비밀번호 확인이 일치하지 않을경우 예외 발생 */
    public static void validate(JoinRequest request) {
        if (request.password.equals(request.confirmPassword) == false) {
            throw new InvalidPasswordException();
        }
    }

}
