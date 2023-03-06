package com.portfolio.request.validator.member;

import com.portfolio.request.member.PasswordChangeRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

public class PasswordChangeValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordChangeRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        PasswordChangeRequest request = (PasswordChangeRequest) target;

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            errors.rejectValue("newPassword", "400", SAME_PASSWORD);
        }

        if (request.getNewPassword()
                .equals(request.getNewPasswordConfirm()) == false) {
            errors.rejectValue("newPasswordConfirm", "400", NOT_MATCHES_NEW_PASSWORD_CONFIRM);
        }
    }
}
