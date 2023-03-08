package com.portfolio.request.validator.member;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.member.ChangePassword;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

@Component
@RequiredArgsConstructor
public class ChangePasswordValidator implements Validator {

    private final MemberUtil memberUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ChangePassword.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePassword request = (ChangePassword) target;
        Member member = memberUtil.getContextMember();

        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String newPasswordConfirm = request.getNewPasswordConfirm();

        /** 이 세가지 모두 null 이 아닌경우 검증 실행 */
        if (currentPassword != null && newPassword != null && newPasswordConfirm != null) {

            /** 입력된 기존 비밀번호가 DB에 저장된 비밀번호와 같지 않을 경우*/
            if (passwordEncoder.matches
                    (currentPassword, member.getPassword()) == false) {
                throw new CustomBadRequestException(INVALID_PASSWORD);
            }
            /** 새 비밀번호와 새 비밀번호 확인이 같지 않을 경우 */
            if (newPassword.equals(newPasswordConfirm) == false) {
                throw new CustomBadRequestException(NOT_MATCHES_NEW_PASSWORD_CONFIRM);

            }
            /** 입력된 기존 비밀번호가 새 비밀번호와 같을 경우 */
            if (currentPassword.equals(newPassword)) {
                throw new CustomBadRequestException(SAME_PASSWORD);
            }
        }
    }
}
