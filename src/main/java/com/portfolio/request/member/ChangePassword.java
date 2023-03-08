package com.portfolio.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class ChangePassword {

    //기존 비밀번호
    @NotBlank(message = "기존 비밀번호를 입력해주세요")
    private String currentPassword;

    //새 비밀번호
    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]{8,20}$",
            message = "비밀번호는 영어, 숫자, 특수문자를 포함해서 8~20자리 이내로 입력해주세요.")
    private String newPassword;


    //비밀번호 확인
    /** newPassword 와 confirmPassword 는 일치해야됨  */
    @NotBlank(message = "새 비밀번호 확인을 입력해주세요")
    private String newPasswordConfirm;


    @Builder
    public ChangePassword(String currentPassword,
                          String newPassword, String newPasswordConfirm) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }
}
