package com.nanum.userservice.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.config.BaseResponse;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(String.valueOf(authentication.getPrincipal()));

        String token = jwtTokenProvider.createToken(authentication, user.getId());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader("token", token);
        response.addHeader("userId", String.valueOf(user.getId()));

        Map<String, String> tokenDto = new HashMap<>();
        tokenDto.put("accessToken", token);

        BaseResponse<Map<String, String>> baseResponse = new BaseResponse<>(tokenDto);

        mapper.writeValue(response.getWriter(), baseResponse);
    }

}
