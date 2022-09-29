package com.nanum.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.config.BaseResponse;
import com.nanum.exception.ExceptionResponse;
import com.nanum.exception.PasswordDismatchException;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.domain.UserEntity;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.BindException;
import java.util.*;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;
    private BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;
    private UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public AuthenticationFilter(UserService userService, Environment env, AuthenticationManager authenticationManager,
                                ObjectMapper mapper,
                                BCryptPasswordEncoder passwordEncoder,
                                UserDetailsService userDetailsService,
                                UserRepository userRepository) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.mapper = mapper;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, PasswordDismatchException {
        try {
            LoginRequest loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
            log.info(loginRequest.getEmail());
            log.info(loginRequest.getPwd());
            UserEntity user = userRepository.findByEmail(loginRequest.getEmail());


            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPwd(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        Claims claims = Jwts.claims().setSubject(String.valueOf(userDetails.getUserId()));
        claims.put("name", userDetails.getName());

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        log.info(env.getProperty("token.secret"));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader("token", token);
        response.addHeader("userId", String.valueOf(userDetails.getUserId()));

        Map<String, String> tokenDto = new HashMap<>();
        tokenDto.put("accessToken", token);

        BaseResponse<Map<String, String>> baseResponse = new BaseResponse<>(tokenDto);
        mapper.writeValue(response.getWriter(), baseResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response
            , AuthenticationException failed) throws IOException, PasswordDismatchException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ExceptionResponse mapBaseResponse = new ExceptionResponse();
        log.info(mapBaseResponse.getMessage());
        log.info(mapBaseResponse.getTimestamp());
        new ObjectMapper().writeValue(response.getOutputStream(), mapBaseResponse);

    }
}
