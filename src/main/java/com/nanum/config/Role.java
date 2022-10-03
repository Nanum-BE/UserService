package com.nanum.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("사용자"),
    HOST("호스트");

    private final String role;

}
