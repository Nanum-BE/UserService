package com.nanum.userservice.user.dto;

import com.nanum.config.Role;
import com.nanum.userservice.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private String pwd;
    private String nickname;
    private Role role;
    private String phone;
    private String gender;
    private boolean isNoteReject;

    public User userDtoToEntity() {
        return User.builder()
                .email(getEmail())
                .pwd(getPwd())
                .nickname(getNickname())
                .role(getRole())
                .phone(getPhone())
                .isNoteReject(isNoteReject)
                .build();
    }
}
