package com.nanum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InformationDismatchException extends IllegalArgumentException {
    private final static String Message = "이메일 혹은 비밀번호가 틀렸습니다";

    public InformationDismatchException() {
        super(Message);
    }
}

