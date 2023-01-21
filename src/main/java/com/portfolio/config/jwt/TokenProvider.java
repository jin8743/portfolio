package com.portfolio.config.jwt;

import com.portfolio.config.auth.CustomUserDetails;
import com.portfolio.config.auth.CustomUserDetailsService;
import com.portfolio.domain.Member;
import com.portfolio.exception.custom.InvalidLoginRequestException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenProvider implements AuthenticationProvider {

    private static final String AUTHORITY = "auth";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 12;

    private static final String ACCESS_USER_ID = "id";

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    private void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new InvalidLoginRequestException();
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getMember(), "", userDetails.getAuthorities());
    }


    public String createToken(Authentication authentication) {
        /**
         * 토큰 발급을 위한 데이터 전달 부분
         */
        String authority = authentication.getAuthorities().toString();
        Member member = (Member) authentication.getPrincipal();

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

    /**
     * @method: 컨텍스트에 해당 유저에 대한 권한 전달하여 접근 허용 또는 거부 진행
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();


        String username = claims.getSubject();
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails.getMember(), "",  userDetails.getAuthorities());
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

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}


