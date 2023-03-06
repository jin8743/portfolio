package com.portfolio.security.handler;

import com.portfolio.exception.custom.AuthenticationFailedException;
import com.portfolio.exception.custom.CustomBadRequestException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.portfolio.exception.custom.AuthenticationFailedException.*;
import static com.portfolio.exception.custom.CustomBadRequestException.*;
import static java.nio.charset.StandardCharsets.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.StringUtils.*;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message = exception.getMessage();
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        response.getWriter().print(
                hasText(message) ? message : INVALID_LOGIN_INFO);
    }
}
