package com.portfolio.request.validator.member;

import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.member.MemberRepository;
import com.portfolio.request.member.SignUpRequest;
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
        return clazz.isAssignableFrom(SignUpRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpRequest request = (SignUpRequest) target;
        if (request.getPassword() == null ) {
            throw new CustomBadRequestException(PASSWORD_NOT_PROVIDED);
        }

        if (request.getPasswordConfirm() == null ||
                request.getPassword().equals(request.getPasswordConfirm()) == false) {
            throw new CustomBadRequestException(NOT_MATCHES_PASSWORD_CONFIRM);
        }

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new CustomBadRequestException(DUPLICATED_USERNAME);
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomBadRequestException(DUPLICATED_EMAIL);
        }
    }
}
