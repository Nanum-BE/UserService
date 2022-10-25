package com.nanum.utils.sms.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nanum.utils.sms.vo.ResponseSMS;

public interface PhoneAuthService {
    ResponseSMS sendRandomMessage(String tel) throws JsonProcessingException;

    ResponseSMS sendTourAndMoveInMessage(String tel, Long status) throws JsonProcessingException;

}
