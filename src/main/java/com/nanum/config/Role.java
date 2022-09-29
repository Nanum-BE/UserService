package com.nanum.config;

public enum Role {
    USER("사용자"),
    HOST("호스트");

    private String role;

    Role(String role){
        this.role = role;
    }

}
