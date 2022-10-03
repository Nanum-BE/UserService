package com.nanum.utils.sms.application;

import com.nanum.utils.sms.presentation.PhoneAuthServiceImpl;
import com.nanum.utils.sms.vo.RequestSMS;
import com.nanum.utils.sms.vo.ResponseSMS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Tag(name = "사용자", description = "사용자 관련 api")
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin
public class PhoneAuthController {
    private final PhoneAuthServiceImpl phoneAuthServiceImpl;
    @Operation(summary = "문자 인증 api", description = "입력받은 번호로 인증번호 요청")
    @PostMapping("/v1/sms/sends")
    public ResponseEntity<ResponseSMS> createMessage(@RequestBody RequestSMS requestSMS) {

        ResponseSMS response = phoneAuthServiceImpl.sendRandomMessage(requestSMS.getPhoneNumber());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "인증번호 확인 api", description = "인증번호를 제대로 입력했는지 확인")
    @PostMapping("/v1/sms/confirm")
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
