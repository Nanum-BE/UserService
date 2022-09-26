package com.nanum.userservice.config;

public enum Role {
    USER("사용자"),
    HOST("호스트");

    private String role;

    Role(String role){
        this.role = role;
    }

}
