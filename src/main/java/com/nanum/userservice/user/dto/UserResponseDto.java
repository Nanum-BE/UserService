package com.nanum.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {
    private String email;
    private String name;
    private String nickName;
    private String phone;
    private boolean isNoteReject;
    private String profileImgUrl;
    private String gender;
    private LocalDateTime createAt;
    private Long userId;
}
