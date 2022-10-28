package com.nanum.exception;

public class UserAlreadyExistException extends RuntimeException {
    private final static String Message = "이미 가입하셨습니다";

    public UserAlreadyExistException() {
        super(Message);
    }
}
