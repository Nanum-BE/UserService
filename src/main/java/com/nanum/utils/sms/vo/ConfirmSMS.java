package com.nanum.utils.sms.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConfirmSMS {
    private String phoneNumber;
    private String content;
}
