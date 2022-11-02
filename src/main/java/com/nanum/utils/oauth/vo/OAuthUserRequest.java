package com.nanum.utils.oauth.vo;

import com.nanum.config.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder

public class OAuthUserRequest {

    private String email;

    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    @Schema(description = "사용자 전화번호", defaultValue = "01012345678")
    @NotNull
    private String phone;

    @NotNull
    @Schema(description = "사용자 성별", defaultValue = "1")
    @Comment("0번은 여자, 1번은 남자로 구분해서 나눈다")
    private String gender;

    private String nickname;

    private Role role;

    private String socialType;

}
