package com.nanum.exception;

public class DuplicateNickNameException extends IllegalArgumentException {
    private final static String Message = "사용중인 닉네임입니다";

    public DuplicateNickNameException() {
        super(Message);
    }
}
