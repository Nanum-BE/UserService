package com.nanum.userservice.user.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude
public class UserResponse {
    private String email;
    private String name;
}
