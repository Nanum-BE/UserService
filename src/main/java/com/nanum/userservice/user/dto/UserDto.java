package com.nanum.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nanum.config.Role;
import com.nanum.userservice.user.domain.User;
import com.nanum.utils.s3.dto.S3UploadDto;
import lombok.*;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private String email;
    private String pwd;
    private String nickname;
    private Role role;
    private String phone;
    private String profileImgUrl;
    private LocalDateTime createAt;
    private String gender;
    private boolean isNoteReject;

    public User toEntity(S3UploadDto s3UploadDto) {
        return User.builder()
                .email(email)
                .pwd(pwd)
                .nickname(nickname)
                .gender(gender)
                .role(role)
                .phone(phone)
                .isNoteReject(isNoteReject)
                .s3UploadDto(s3UploadDto)
                .build();
    }

}
