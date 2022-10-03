package com.nanum.exception;

public class ProfileImgNotFoundException extends RuntimeException {
    private final static String Message = "선택한 사진이 없습니다";

    public ProfileImgNotFoundException() {
        super(Message);
    }
}