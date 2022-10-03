package com.nanum.userservice.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModifyRequest {
    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    @Schema(description = "사용자 전화번호", defaultValue = "01012345678")
    @NotNull
    private String phone;

    @NotNull(message = "Nickname cannot be null")
    @Schema(description = "사용자 닉네임", defaultValue = "아무거나 적어줘여")
    @Size(min = 1,message = "Nickname must be not be less than two characters")
    private String nickname;

    @NotNull
    @Schema(description = "사용자 성별", defaultValue = "1")
    @Comment("0번은 여자, 1번은 남자로 구분해서 나눈다")
    private String gender;
    
    //TODO 쪽지수신여부가 true, false가 안바뀜
    @NotNull
    @Schema(description = "쪽지수신 허용 여부", defaultValue = "false")
    @Comment("true false로 쪽지 수신 허용 여부를 나눈다")
    private boolean isNoteReject;
}
