package com.portfolio.config.jwt;

import com.portfolio.config.auth.CustomUserDetails;
import com.portfolio.config.auth.CustomUserDetailsService;
import com.portfolio.domain.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component
public class  JwtUtil {

    //토큰 유효시간 1시간으로 설정
    private static final String ACCESS_USER_ID = "id";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000;
    private static final String AUTHORITY = "auth";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    private final CustomUserDetailsService customUserDetailsService;


    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }



    public String createToken(Authentication authentication) {

        /**
         * 토큰 발급을 위한 데이터 전달 부분
         */
        String authority = authentication.getAuthorities().toString();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = customUserDetails.getMember();

        long now = (new Date()).getTime();
        Date tokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(member.getUsername())
                .claim(AUTHORITY, authority)
                .claim(ACCESS_USER_ID, member.getId())
                .setExpiration(tokenExpiresIn)
                .signWith(key)
                .compact();
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("토큰이 잘못되었습니다.");
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
