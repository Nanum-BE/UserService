package com.nanum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateEmailException extends IllegalArgumentException {
    private final static String Message = "사용중인 이메일입니다";

    public DuplicateEmailException() {
        super(Message);
    }
}
