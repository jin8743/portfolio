package com.portfolio.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp="[a-zA-Z1-9]{3,20}",
            message = "아이디는 영어와 숫자를 포함하여 3~20자리 이내로 입력해주세요.")
    private String username;


    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "[a-zA-Z1-9]{8,20}",
            message = "비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요.")
    private String password;

    @Builder
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
