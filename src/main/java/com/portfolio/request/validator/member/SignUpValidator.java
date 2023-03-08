package com.portfolio.request.validator.member;

import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.SignUp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

@Component
@RequiredArgsConstructor
public class SignUpValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUp.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUp request = (SignUp) target;

        /** 이 4가지 모두 null 이 아닐 경우 검증 진행 */
        String password = request.getPassword();
        String passwordConfirm = request.getPasswordConfirm();
        String username = request.getUsername();
        String email = request.getEmail();

        if (notNull(request)) {
            /** 비밀번호와 비밀번호 확인이 일치하지 않을 경우 */
            if (password.equals(passwordConfirm) == false) {
                throw new CustomBadRequestException(NOT_MATCHES_PASSWORD_CONFIRM);
            }
            /** 입력한 아이디가 이미 사용중인 경우 */
            if (memberRepository.existsByUsername(username)) {
                throw new CustomBadRequestException(DUPLICATED_USERNAME);
            }
            /** 입력한 이메일이 이미 사용중인 경우 */
            if (memberRepository.existsByEmail(email)) {
                throw new CustomBadRequestException(DUPLICATED_EMAIL);
            }
        }
    }

    private boolean notNull(SignUp request) {
        return request.getUsername() != null && request.getEmail() != null &&
                request.getPassword() != null && request.getPasswordConfirm() != null;
    }
}
