package com.nanum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class InformationDismatchException extends AuthenticationException {
    private final static String Message = "이메일 틀렸습니다";

    public InformationDismatchException() {
        super(Message);
    }
}

