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
    @Schema(description = "이메일", defaultValue = "abcde@naver.com")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name not be less than two characters")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equal or grater than 8 characters")
    private String pwd;

    @NotNull(message = "Nickname cannot be null")
    @Size(min = 1,message = "Nickname must be not be less than two characters")
    private String nickname;

    @NotNull
    private String role;

    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    @NotNull
    private String phone;

    @NotNull
    @Comment("1번은 남자, 2번은 여자로 구분해서 나눈다")
    private int gender;
}
