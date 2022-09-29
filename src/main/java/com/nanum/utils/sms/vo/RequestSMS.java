package com.nanum.utils.sms.vo;

import lombok.Getter;

@Getter
public class RequestSMS {
    private String phoneNumber;
    private String content;
}
