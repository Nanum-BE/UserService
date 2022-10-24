package com.nanum.userservice.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    @Schema(description = "사용자 식별 id")
    private Long id;

    @Schema(description = "사용자 이메일", defaultValue = "spharos@gmail.com")
    private String email;

    @Schema(description = "사용자 별명")
    private String nickName;

    @Schema(description = "사용자 전화번호")
    private String phone;

    @Schema(description = "사용자 쪽지수신 허용 여부")
    private boolean isNoteReject;

    @Schema(description = "사용자 프로필 이미지")
    private String profileImgUrl;

    @Schema(description = "사용자 성별")
    private String gender;

    @Schema(description = "계정 생성일자")
    private LocalDateTime createAt;
}
