package com.portfolio.security.provider;

import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.security.service.CustomUser;
import com.portfolio.security.service.CustomUserDetailsService;
import com.portfolio.security.token.CustomAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.portfolio.exception.custom.CustomBadRequestException.*;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usernameOrEmail = authentication.getName();
        String password = (String) authentication.getCredentials();

        CustomUser customUser = (CustomUser) userDetailsService.loadUserByUsername(usernameOrEmail);

        if (passwordEncoder
                .matches(password, customUser.getPassword()) == false) {
            throw new BadCredentialsException(INVALID_LOGIN_INFO);
        }

        return new CustomAuthenticationToken(customUser,
                null, customUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomAuthenticationToken.class);
    }

}
