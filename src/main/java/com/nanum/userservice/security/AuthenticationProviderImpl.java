package com.nanum.userservice.security;

import com.nanum.exception.InformationDismatchException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.jwt.UserDetailServiceImpl;
import com.nanum.utils.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        // 전달 받은 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        // AuthenticaionFilter에서 생성된 토큰으로부터 아이디와 비밀번호를 추출
        String username = token.getName();
        String password = (String) token.getCredentials();

        UserDetailsImpl userDetail;

        User userEmail = userRepository.findByEmail(username);
        userDetail = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        if (!bCryptPasswordEncoder.matches(password, userEmail.getPwd())) {
            throw new InformationDismatchException();
        }

        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(), "", userDetail.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
