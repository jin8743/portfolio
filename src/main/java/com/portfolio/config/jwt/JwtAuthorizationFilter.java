package com.portfolio.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String jwt = jwtUtil.resolveToken(request);

        if (jwt == null) {
            log.info("jwt가 존재하지 않습니다");
            chain.doFilter(request, response);
            return;
        }

        if (jwtUtil.validateToken(jwt)) {

            //Jwt 토큰 검증을 통해서 정상인 경우 Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication = jwtUtil.getAuthenticationToken(jwt);

            //강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("authentication 저장 완료");
        }
        chain.doFilter(request, response);
    }
}
