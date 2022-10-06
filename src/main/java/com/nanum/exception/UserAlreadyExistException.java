package com.nanum.exception;


import org.springframework.security.core.AuthenticationException;

public class UserAlreadyExistException extends AuthenticationException {
    private final static String Message = "이미 가입하셨습니다";

    public UserAlreadyExistException() {
        super(Message);
    }
}
