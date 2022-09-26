package com.nanum.userservice.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude
public class ResponseUser {
    private String email;
    private String name;
}
