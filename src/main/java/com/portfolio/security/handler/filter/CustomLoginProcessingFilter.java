package com.portfolio.security.handler.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.request.member.LoginRequest;
import com.portfolio.security.token.CustomAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.portfolio.exception.custom.CustomBadRequestException.*;
import static javax.servlet.http.HttpServletResponse.*;

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
           LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);

            if (StringUtils.hasText(loginRequest.getUsernameOrEmail()) == false
                    || StringUtils.hasText(loginRequest.getPassword()) == false) {

                throw new AuthenticationServiceException(USERNAME_OR_PASSWORD_NOT_PROVIDED);
            }

            CustomAuthenticationToken token = new CustomAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword());

            return this.getAuthenticationManager().authenticate(token);

        } catch (IOException e) {
            throw new InsufficientAuthenticationException(INVALID_LOGIN_FORMAT) {
            };
        }


    }

}
