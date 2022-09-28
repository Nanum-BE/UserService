package com.nanum.userservice.user.dto;

import com.nanum.config.Role;
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
    private String name;
    private String pwd;
    private String nickname;
    private String profileImgUrl;
    private Role role;
    private String phone;
    private String gender;

}
