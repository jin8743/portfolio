package com.portfolio.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.domain.Member;
import com.portfolio.exception.custom.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.portfolio.config.jwt.JwtUtil.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            Member member = objectMapper.readValue(request.getInputStream(), Member.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            member.getUsername(),
                            member.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new InvalidRequestException();
        }
    }

    // JWT 토큰 생성하여 response에 담기
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String jwt = jwtUtil.createToken(authResult);

        response.setHeader(AUTHORIZATION_HEADER, "Bearer " + jwt);
    }
}
