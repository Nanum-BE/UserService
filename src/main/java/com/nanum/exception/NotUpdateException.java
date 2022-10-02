package com.nanum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotUpdateException extends RuntimeException {
    public NotUpdateException(String format) {
        super((format));
    }
}
