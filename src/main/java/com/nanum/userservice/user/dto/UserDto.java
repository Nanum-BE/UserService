package com.nanum.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nanum.config.Role;
import com.nanum.userservice.user.domain.User;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long userId;
    private String email;
    private String pwd;
    private String nickname;
    private Role role;
    private String phone;
    private String profileImgUrl;
    private LocalDateTime createAt;
    private String gender;
    private boolean isNoteReject;

}
