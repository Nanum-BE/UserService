package com.nanum.exception;


public class DuplicateEmailException extends IllegalArgumentException {
    private final static String Message = "사용중인 이메일입니다";

    public DuplicateEmailException() {
        super(Message);
    }
}
