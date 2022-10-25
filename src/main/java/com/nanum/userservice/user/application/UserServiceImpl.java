package com.nanum.userservice.user.application;

import com.nanum.config.Role;
import com.nanum.exception.ProfileImgNotFoundException;
import com.nanum.exception.UserNotFoundException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.ModifyPasswordRequest;
import com.nanum.userservice.user.vo.UserModifyRequest;
import com.nanum.userservice.user.vo.UserResponse;
import com.nanum.userservice.user.vo.UsersResponse;
import com.nanum.utils.oauth.vo.OAuthUserRequest;
import com.nanum.utils.s3.S3UploaderService;
import com.nanum.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3UploaderService s3UploaderService;

    @Override
    public boolean createUser(UserDto userDto, MultipartFile multipartFile) {
        S3UploadDto s3UploadDto;

        if (multipartFile != null) {
            try {
                s3UploadDto = s3UploaderService.upload(multipartFile, "myspharosbucket", "userProfile");

                User newUser = User.builder()
                        .email(userDto.getEmail())
                        .pwd(bCryptPasswordEncoder.encode(userDto.getPwd()))
                        .nickname(userDto.getNickname())
                        .role(userDto.getRole())
                        .phone(userDto.getPhone())
                        .gender(userDto.getGender())
                        .isNoteReject(userDto.isNoteReject())
                        .profileImgPath(s3UploadDto.getImgUrl())
                        .saveName(s3UploadDto.getSaveName())
                        .originName(s3UploadDto.getOriginName())
                        .build();

                userRepository.save(newUser);

            } catch (IOException e) {
                throw new ProfileImgNotFoundException();
            }
        } else {
            userRepository.save(User.builder()
                    .email(userDto.getEmail())
                    .pwd(bCryptPasswordEncoder.encode(userDto.getPwd()))
                    .isNoteReject(userDto.isNoteReject())
                    .role(userDto.getRole())
                    .nickname(userDto.getNickname())
                    .phone(userDto.getPhone())
                    .gender(userDto.getGender())
                    .build());
        }

        return true;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkNickName(String nickName) {
        return userRepository.existsByNickname(nickName);
    }

    @Override
    public void modifyUser(Long userId, UserModifyRequest request, MultipartFile file) {
        User user = userRepository.findById(userId).get();

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        S3UploadDto s3UploadDto;

        if (file != null) {
            try {
                s3UploadDto = s3UploaderService.upload(file, "myspharosbucket", "userProfile");

                userRepository.save(User.builder()
                        .id(userId)
                        .email(user.getEmail())
                        .role(user.getRole())
                        .loginFailCnt(user.getLoginFailCnt())
                        .warnCnt(user.getWarnCnt())
                        .nickname(request.getNickname())
                        .phone(request.getPhone())
                        .profileImgPath(s3UploadDto.getImgUrl())
                        .saveName(s3UploadDto.getSaveName())
                        .originName(s3UploadDto.getOriginName())
                        .pwd(user.getPwd())
                        .isNoteReject(request.getIsNoteReject())
                        .gender(request.getGender())
                        .build());

            } catch (IOException e) {
                throw new ProfileImgNotFoundException();
            }
        } else

            userRepository.save(User.builder()
                    .id(userId)
                    .email(user.getEmail())
                    .role(user.getRole())
                    .loginFailCnt(user.getLoginFailCnt())
                    .warnCnt(user.getWarnCnt())
                    .nickname(request.getNickname())
                    .phone(request.getPhone())
                    .profileImgPath(request.getImgUrl())
                    .saveName(user.getSaveName())
                    .originName(user.getOriginName())
                    .pwd(user.getPwd())
                    .isNoteReject(request.getIsNoteReject())
                    .gender(request.getGender())
                    .build());
    }

    @Override
    public void modifyUserPw(Long userId, ModifyPasswordRequest passwordRequest) {
        User user = userRepository.findById(userId).get();

        userRepository.save(User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .gender(user.getGender())
                .warnCnt(user.getWarnCnt())
                .loginFailCnt(user.getLoginFailCnt())
                .role(user.getRole())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .profileImgPath(user.getProfileImgPath())
                .isNoteReject(user.getIsNoteReject())
                .pwd(bCryptPasswordEncoder.encode(passwordRequest.getNewPw()))
                .build());
    }

    @Override
    public User signOAuthUser(OAuthUserRequest userRequest) {

        User user = userRepository.save(User.builder()
                .email(userRequest.getEmail())
                .gender(userRequest.getGender())
                .role(Role.USER)
                .isNoteReject(false)
                .loginFailCnt(0)
                .warnCnt(0)
                .nickname(userRequest.getNickname())
                .socialType(userRequest.getSocialType())
                .phone(userRequest.getPhone())
                .pwd(bCryptPasswordEncoder.encode("1"))
                .build());

        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UsersResponse> retrieveAllUsers() {
        List<User> userEntities = userRepository.findAll();
        return userEntities.stream().map(UsersResponse::of).collect(Collectors.toList());
    }

    // 박찬흠 수정함
    @Override
    public UserResponse retrieveUser(Long userId) {

        Optional<User> userEntity = userRepository.findById(userId);

        if (userEntity.isEmpty()) {
            throw new UserNotFoundException(String.format("ID[%s] not found", userId));
        }
        User user = userEntity.get();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickname())
                .phone(user.getPhone())
                .isNoteReject(user.getIsNoteReject())
                .profileImgUrl(user.getProfileImgPath())
                .gender(user.getGender())
                .createAt(user.getCreateAt())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UsersResponse retrieveUsers(String email) {

        User user = userRepository.findByEmail(email);

        return UsersResponse.of(user);
    }

    @Override
    public List<UsersResponse> retrieveUsersByUserIds(List Longs) {
        List<User> users = userRepository.findAllById(Longs);
        if (users.size() < 1) {
            throw new UserNotFoundException(String.format("users[%s] not found", Longs));
        }

        return users.stream()
                .map(UsersResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> retrieveUserInfoByIds(List<Long> userId) {
        List<User> users = userId
                .stream()
                .map(a -> userRepository.findById(a).get())
                .collect(Collectors.toList());

        return users.stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

    }
}
