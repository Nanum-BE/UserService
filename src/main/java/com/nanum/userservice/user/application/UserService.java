package com.nanum.userservice.user.application;

import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.vo.*;
import com.nanum.utils.oauth.vo.OAuthUserRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    boolean createUser(UserDto userDto, MultipartFile multipartFile);

    boolean checkEmail(String email);

    boolean checkNickName(String nickName);

    void modifyUser(Long userId, UserModifyRequest request, MultipartFile file);

    void modifyUserPw(Long userId, ModifyPasswordRequest passwordRequest);

    User signOAuthUser(OAuthUserRequest userRequest, MultipartFile multipartFile);

    List<UsersResponse> retrieveAllUsers();

    UserResponse retrieveUser(Long userId);

    UsersResponse retrieveUsers(String email);

    // 박
    List<UsersResponse> retrieveUsersByUserIds(List Longs);

    List<UserResponse> retrieveUserInfoByIds(List<Long> userId);

    UserPhoneResponse retrievePhoneByEmail(String email);

}
