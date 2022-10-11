package com.nanum.userservice.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.config.BaseResponse;
import com.nanum.exception.ExceptionResponse;
import com.nanum.exception.InformationDismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, InformationDismatchException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(204);
        LocalDateTime date = LocalDateTime.now();

        BaseResponse<HttpServletResponse> responseBaseResponse = new BaseResponse<>(response);

        log.info(String.valueOf(response.getStatus()));

        mapper.writeValue(response.getWriter(), responseBaseResponse);
    }
}
