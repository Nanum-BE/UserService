//package com.nanum.userservice.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nanum.config.BaseResponse;
//import com.nanum.exception.ExceptionResponse;
//import com.nanum.exception.InformationDismatchException;
//import com.nanum.userservice.user.application.UserService;
//import com.nanum.userservice.user.dto.UserDto;
//import com.nanum.userservice.user.vo.LoginRequest;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private final UserService userService;
//    private final Environment env;
//    private final ObjectMapper mapper;
//    UsernamePasswordAuthenticationToken authRequest;
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request,
//                                                HttpServletResponse response)
//            throws AuthenticationException, InformationDismatchException {
//
//
//        try {
//            LoginRequest loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);
//            authRequest = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPwd());
//            log.info(loginRequest.getEmail());
//            log.info(loginRequest.getPwd());
//            log.info(authRequest.getName());
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        setDetails(request, authRequest);
//        return this.getAuthenticationManager().authenticate(authRequest);
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request,
//                                            HttpServletResponse response,
//                                            FilterChain chain,
//                                            Authentication authResult) throws IOException, ServletException {
//        String userName = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
//        UserDto userDetails = userService.getUserDetailsByEmail(userName);
//
//        Claims claims = Jwts.claims().setSubject(String.valueOf(userDetails.getUserId()));
////        claims.put("name", userDetails.getEmail());
//
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .setExpiration(new Date(System.currentTimeMillis() +
//                        Long.parseLong(env.getProperty("token.expiration_time"))))
//                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
//                .compact();
//
//        log.info(env.getProperty("token.secret"));
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//        response.addHeader("token", token);
//        response.addHeader("userId", String.valueOf(userDetails.getUserId()));
//
//        Map<String, String> tokenDto = new HashMap<>();
//        tokenDto.put("accessToken", token);
//
//        BaseResponse<Map<String, String>> baseResponse = new BaseResponse<>(tokenDto);
//        mapper.writeValue(response.getWriter(), baseResponse);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response
//            , AuthenticationException failed) throws IOException, InformationDismatchException {
//
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        LocalDateTime date = LocalDateTime.now();
//
//        ExceptionResponse mapBaseResponse = new ExceptionResponse();
//        mapBaseResponse.setMessage("????????? ?????? ??????????????? ???????????????");
//        mapBaseResponse.setTimestamp(String.valueOf(date));
//        mapBaseResponse.setDetails("BAD REQUEST");
//        log.info(mapBaseResponse.getMessage());
//
//
//        new ObjectMapper().writeValue(response.getOutputStream(), mapBaseResponse);
//
//    }
//}
