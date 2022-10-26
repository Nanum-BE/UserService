package com.nanum.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.exception.InformationDismatchException;
import com.nanum.userservice.user.dto.UserLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, InformationDismatchException e) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        final UsernamePasswordAuthenticationToken authRequest;

        final UserLoginDto userLoginDto;
        try {
            // 사용자 요청 정보로 UserPasswordAuthenticationToken 발급
            userLoginDto = new ObjectMapper().readValue(request.getInputStream(), UserLoginDto.class);
            authRequest = new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPwd());
        } catch (IOException e) {
            throw new InformationDismatchException();
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}