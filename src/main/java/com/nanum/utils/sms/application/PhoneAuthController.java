package com.nanum.utils.sms.application;

import com.nanum.utils.sms.presentation.PhoneAuthServiceImpl;
import com.nanum.utils.sms.vo.RequestSMS;
import com.nanum.utils.sms.vo.ResponseSMS;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PhoneAuthController {
    private final PhoneAuthServiceImpl phoneAuthServiceImpl;

    @PostMapping("/sms/sends")
    public ResponseEntity<ResponseSMS> createMessage(@RequestBody RequestSMS requestSMS) {

        ResponseSMS response = phoneAuthServiceImpl.sendRandomMessage(requestSMS.getPhoneNumber());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/sms/confirm")
    public ResponseEntity<String> ConfirmMessage(@RequestBody RequestSMS requestSMS) {
        String message = phoneAuthServiceImpl.confirmMessage(requestSMS);
        String value;

        if (Objects.equals(message, "ok")) {
            value = "인증번호 확인되었습니다";
        } else {
            value = "인증번호를 다시 확인해주세요";
        }

        return ResponseEntity.status(HttpStatus.OK).body(value);
    }
}
