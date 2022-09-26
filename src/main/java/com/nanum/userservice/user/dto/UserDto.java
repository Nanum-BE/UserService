package com.nanum.userservice.user.dto;

import lombok.Data;


@Data
public class UserDto {
    private Long userId;
    private String email;
    private String name;
    private String pwd;
    private String nickname;
    private String profileImgUrl;
    private String role;
    private String phone;
    private int gender;

}
