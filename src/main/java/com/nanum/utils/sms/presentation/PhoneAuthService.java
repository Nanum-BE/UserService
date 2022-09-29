package com.nanum.utils.sms.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nanum.utils.sms.vo.ResponseSMS;

public interface PhoneAuthService {
    ResponseSMS sendRandomMessage(String tel) throws JsonProcessingException;

}
