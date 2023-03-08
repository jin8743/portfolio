package com.portfolio.security.handler.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.request.member.Login;
import com.portfolio.security.token.CustomAuthenticationToken;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

public class CustomLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/api/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if (HttpMethod.POST.name().equals(request.getMethod()) == false) {
            throw new AuthenticationException(LOGIN_METHOD_NOT_SUPPORTED) {
            };
        }

        try {
           Login login = objectMapper.readValue(request.getReader(), Login.class);

            if (StringUtils.hasText(login.getUsernameOrEmail()) == false
                    || StringUtils.hasText(login.getPassword()) == false) {

                throw new AuthenticationServiceException(USERNAME_OR_PASSWORD_NOT_PROVIDED);
            }

            CustomAuthenticationToken token = new CustomAuthenticationToken(
                    login.getUsernameOrEmail(),
                    login.getPassword());

            return this.getAuthenticationManager().authenticate(token);

        } catch (IOException e) {
            throw new InsufficientAuthenticationException(INVALID_LOGIN_FORMAT) {
            };
        }


    }

}
