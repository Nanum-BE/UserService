package com.nanum.userservice.user.dto;

import com.nanum.config.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Data
public class UserOriginDto {
    private Long id;
    private String email;
    private String name;
    private String pwd;
    private String nickname;
    private String profileImgPath;
    private Role role;
    private String phone;
    private String gender;
    private boolean isNoteReject;
    private int warnCnt;
    private int loginFailCnt;
}
