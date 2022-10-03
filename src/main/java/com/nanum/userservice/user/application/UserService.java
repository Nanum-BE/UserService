package com.nanum.userservice.user.application;

import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.vo.ModifyPasswordRequest;
import com.nanum.userservice.user.vo.UserModifyRequest;
import com.nanum.userservice.user.vo.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends UserDetailsService {
    void createUser(UserDto userDto, MultipartFile multipartFile);

    boolean checkEmail(String email);

    boolean checkNickName(String nickName);

    void modifyUser(Long userId, UserModifyRequest request, MultipartFile file);

    void modifyUserPw(Long userId, ModifyPasswordRequest passwordRequest);

    UserDto getUserDetailsByEmail(String email);

    List<UserResponse> retrieveAllUsers();

    UserResponse retrieveUser(Long userId);
}
