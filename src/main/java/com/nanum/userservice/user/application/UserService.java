package com.nanum.userservice.user.application;

import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.vo.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    void createUser(UserDto userDto);

    boolean checkEmail(String email);

    boolean checkNickName(String nickName);

    UserDto getUserDetailsByEmail(String email);

    List<UserResponse> retrieveAllUsers();

    UserResponse retrieveUser(Long userId);
}
