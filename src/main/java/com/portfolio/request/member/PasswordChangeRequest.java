package com.portfolio.request.member;

import com.portfolio.exception.custom.InvalidPasswordChangeRequestException;
import com.portfolio.exception.custom.PasswordChangeConfirmFailedException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {

    //기존 비밀번호
    private String currentPassword;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "[a-zA-Z1-9]{8,20}",
            message = "비밀번호는 영어와 숫자로 포함해서 8~20자리 이내로 입력해주세요.")
    //새 비밀번호
    private String newPassword;

    //비밀번호 확인
    /** changePassword 와 confirmPassword 는 일치해야됨  */
    private String newPasswordConfirm;


    @Builder
    public PasswordChangeRequest(String currentPassword,
                                 String newPassword, String newPasswordConfirm) {

        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public static void validatePassword(PasswordChangeRequest request) {

        /** 기존 비밀번호와 새 비밀번호가 서로 일치할 경우 예외 발생*/
        if (request.currentPassword.equals(request.newPassword)) {
            throw new InvalidPasswordChangeRequestException();
        }

        /** 새 비밀번호와 새 비밀번호 확인이 서로 일치하지 않을경우 예외 발생*/
        if (request.newPassword.equals(request.newPasswordConfirm) == false) {
            throw new PasswordChangeConfirmFailedException();
        }
    }

}
