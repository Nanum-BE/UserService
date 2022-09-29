package com.nanum.userservice.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude
public class UserResponse {
    @Schema(description = "사용자 이메일", defaultValue = "spharos@gmail.com")
    private String email;

    @Schema(description = "사용자 이름")
    private String name;

    @Schema(description = "사용자 전화번호")
    private String phone;

    @Schema(description = "사용자 쪽지수신 허용 여부")
    private String isNoteReject;
}
