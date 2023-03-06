package com.portfolio.security.handler;

import com.portfolio.exception.custom.AuthorizationFailedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.portfolio.exception.custom.AuthorizationFailedException.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(SC_FORBIDDEN);
        response.setCharacterEncoding(UTF_8.name());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().print(MESSAGE);
    }
}
