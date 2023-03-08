package com.portfolio.request.validator.member;

import com.portfolio.domain.Member;
import com.portfolio.exception.custom.AuthorizationFailedException;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.member.Unregister;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

@Component
@RequiredArgsConstructor
public class UnregisterValidator implements Validator {

    private final MemberUtil memberUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Unregister.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Unregister request = (Unregister) target;
        Member member = memberUtil.getContextMember();

        /** 입력한 비밀번호가 null 이 아니지만, DB에 저장된 비밀번호와 같지 않을 경우 */
        if (request.getPassword() != null && passwordEncoder.matches
                (request.getPassword(), member.getPassword()) == false) {
            throw new CustomBadRequestException(INVALID_PASSWORD);
        }
    }
}
