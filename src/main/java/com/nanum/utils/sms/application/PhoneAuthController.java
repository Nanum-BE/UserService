package com.nanum.utils.sms.application;

import com.nanum.config.BaseResponse;
import com.nanum.exception.UserAlreadyExistException;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.sms.presentation.PhoneAuthServiceImpl;
import com.nanum.utils.sms.vo.ConfirmSMS;
import com.nanum.utils.sms.vo.RequestSMS;
import com.nanum.utils.sms.vo.ResponseSMS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@Tag(name = "사용자", description = "사용자 문자인증과 관련한 api")
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "201", description = "created successfully",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "bad request",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "500", description = "server error",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
})
public class PhoneAuthController {

    private final PhoneAuthServiceImpl phoneAuthServiceImpl;
    private final UserRepository userRepository;

    @Operation(summary = "문자 인증 api", description = "입력받은 번호로 인증번호 요청")
    @PostMapping("/v1/sms/sends")
    public ResponseEntity<ResponseSMS> createMessage(@RequestBody RequestSMS requestSMS) {

        ResponseSMS response = phoneAuthServiceImpl.sendRandomMessage(requestSMS.getPhoneNumber());
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "인증번호 확인 api", description = "인증번호를 제대로 입력했는지 확인")
    @PostMapping("/v1/sms/confirm")
    public ResponseEntity<String> ConfirmMessage(@RequestBody ConfirmSMS requestSMS) {

        if (userRepository.existsByPhone(requestSMS.getPhoneNumber())) {
            throw new UserAlreadyExistException();
        }

        ResponseEntity<String> body;
        String message = phoneAuthServiceImpl.confirmMessage(requestSMS);
        String value;

        if (Objects.equals(message, "ok")) {
            value = "인증번호 확인되었습니다";
            body = ResponseEntity.status(HttpStatus.OK).body(value);
        } else {
            value = "인증번호를 다시 확인해주세요";
            body = ResponseEntity.status(HttpStatus.NO_CONTENT).body(value);
        }

        return body;
    }
}
