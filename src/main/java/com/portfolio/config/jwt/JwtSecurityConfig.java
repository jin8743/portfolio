package com.portfolio.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@EnableWebSecurity
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final CorsConfig corsConfig;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //HttpBasic 방식 비활성화
                .httpBasic().disable()

                //FormLogin 방식 비활성화
                .formLogin().disable()

                //토큰을 사용하는 방식이므로 csrf 비활성화
                .csrf().disable()

                .apply(new MyCustomDsl())

                //세션을 사용하지 않기 떄문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/login", "/", "/join").permitAll()
                .anyRequest().authenticated()


                .and();


        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, objectMapper, jwtUtil))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, jwtUtil));
        }
    }
}
