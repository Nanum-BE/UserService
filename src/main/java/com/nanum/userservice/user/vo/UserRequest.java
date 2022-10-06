package com.nanum.userservice.user.vo;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.Comment;


@Data
public class UserRequest {
    @NotNull(message = "Email cannot be null")
    @Schema(description = "사용자 이메일", defaultValue = "spharos@gmail.com")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Password cannot be null")
    @Schema(description = "사용자 비밀번호", defaultValue = "123456789")
    @Size(min = 8, message = "Password must be equal or grater than 8 characters")
    private String pwd;

    @NotNull(message = "Nickname cannot be null")
    @Schema(description = "사용자 닉네임", defaultValue = "별명")
    @Size(min = 1,message = "Nickname must be not be less than two characters")
    private String nickname;

    @NotNull
    @Schema(description = "사용자 권한", defaultValue = "USER")
    private String role;

    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    @Schema(description = "사용자 전화번호", defaultValue = "01012345678")
    @NotNull
    private String phone;

    @NotNull
    @Schema(description = "사용자 성별", defaultValue = "M")
    @Comment("남자는 M, 여자는 F로")
    private String gender;

    @NotNull
    @Schema(description = "쪽지수신 허용 여부", defaultValue = "false")
    @Comment("true false로 쪽지 수신 허용 여부를 나눈다")
    private boolean isNoteReject;
}
