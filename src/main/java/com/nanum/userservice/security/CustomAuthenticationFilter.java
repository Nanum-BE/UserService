package com.nanum.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.exception.ExceptionResponse;
import com.nanum.exception.InformationDismatchException;
import com.nanum.userservice.user.dto.UserLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, InformationDismatchException e) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, InformationDismatchException {
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
        log.info("********");
        log.info(userLoginDto.getEmail());
        log.info(userLoginDto.getPwd());
        log.info(authRequest.getName());
        log.info(String.valueOf(authRequest.getPrincipal()));
        // AuthenticationManager에게 전달 -> AuthenticationProvider의 인증 메서드 실행
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}