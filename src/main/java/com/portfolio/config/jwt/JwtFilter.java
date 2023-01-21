package com.portfolio.config.jwt;

import com.portfolio.exception.custom.AuthenticationFailedException;
import com.portfolio.exception.custom.InvalidJwtRequest;
import com.portfolio.exception.custom.InvalidLoginRequestException;
import com.portfolio.exception.custom.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter  extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "post");

    public static final String AUTHORIZATION_HEADER = "Authorization";


    private final AuthenticationManager authenticationManager;


    public JwtFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        UsernamePasswordAuthenticationToken unauthenticatedToken = createUnauthenticatedToken(request);
        return authenticationManager.authenticate(unauthenticatedToken);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        String jwt = resolveToken(httpReq);
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new InvalidJwtRequest();
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private UsernamePasswordAuthenticationToken createUnauthenticatedToken(HttpServletRequest request) {

        if (!request.getMethod().equals("POST")) {
            throw new InvalidRequestException();
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    private String obtainUsername(HttpServletRequest request) {
        return request.getParameter("username");
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter("password");
    }
}
