package com.nanum.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;
    private PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;
    private UserRepository userRepository;

    public AuthenticationFilter(UserService userService, Environment env, AuthenticationManager authenticationManager,
                                ObjectMapper mapper, PasswordEncoder passwordEncoder) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest creds = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
//            UserDto userDetailsByEmail = userService.getUserDetailsByEmail(creds.getEmail());
//            com.nanum.userservice.user.domain.User user = userRepository.findById(userDetailsByEmail.getUserId()).get();
//            if (!passwordEncoder.matches(creds.getPwd(), user.getPwd())) {
//                throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
//            }
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPwd(),
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

        mapper.writeValue(response.getWriter(), tokenDto);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response
            , AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
