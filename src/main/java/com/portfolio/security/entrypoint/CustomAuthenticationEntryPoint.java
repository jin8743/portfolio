package com.portfolio.security.entrypoint;

import com.portfolio.exception.custom.AuthenticationFailedException;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.portfolio.exception.custom.AuthenticationFailedException.*;
import static java.nio.charset.StandardCharsets.*;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.*;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(SC_UNAUTHORIZED);
        response.setCharacterEncoding(UTF_8.name());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().print(MESSAGE);
    }
}
