package com.nanum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateNickNameException extends IllegalArgumentException {
    private final static String Message = "사용중인 닉네임입니다";

    public DuplicateNickNameException() {
        super(Message);
    }
}
