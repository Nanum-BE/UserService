package com.nanum.userservice.user.application;

import com.nanum.exception.ProfileImgNotFoundException;
import com.nanum.exception.UserNotFoundException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.*;
import com.nanum.utils.oauth.vo.OAuthUserRequest;
import com.nanum.utils.s3.S3UploaderService;
import com.nanum.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final S3UploaderService s3UploaderService;

    //회원가입
    @Transactional
    @Override
    public boolean createUser(UserDto userDto, MultipartFile multipartFile) {
        S3UploadDto s3UploadDto;

        if (multipartFile != null) {
            try {
                s3UploadDto = s3UploaderService.upload(multipartFile, "myspharosbucket", "userProfile");
                userRepository.save(userDto.toEntity(s3UploadDto));
            } catch (IOException e) {
                throw new ProfileImgNotFoundException();
            }
        } else {
            userRepository.save(userDto.toEntity(null));
        }
        return true;
    }

    //이메일 중복 검사
    @Transactional
    @Override
    public boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    //닉네임 중복 검사
    @Transactional
    @Override
    public boolean checkNickName(String nickName) {
        return userRepository.existsByNickname(nickName);
    }

    //회원정보수정
    @Transactional
    @Override
    public void modifyUser(Long userId, UserModifyRequest request, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        S3UploadDto s3UploadDto;

        if (file != null) {
            try {
                s3UploadDto = s3UploaderService.upload(file, "myspharosbucket", "userProfile");
                user.modifyUserWithImg(request, s3UploadDto);
            } catch (IOException e) {
                throw new ProfileImgNotFoundException();
            }
        } else
            user.modUser(request);
    }

    //비밀번호 변경
    @Transactional
    @Override
    public void modifyUserPw(Long userId, ModifyPasswordRequest passwordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        user.changePw(passwordRequest);
    }

    //소셜 로그인 회원 등록
    @Transactional
    @Override
    public User signOAuthUser(OAuthUserRequest userRequest, MultipartFile multipartFile) {
        S3UploadDto s3UploadDto;
        User user;
        if (multipartFile != null) {
            try {
                s3UploadDto = s3UploaderService.upload(multipartFile, "myspharosbucket", "userProfile");
                user = userRequest.toEntity(s3UploadDto);
                userRepository.save(user);
            } catch (IOException e) {
                throw new ProfileImgNotFoundException();
            }
        } else {
            user = userRequest.toEntity(null);
            userRepository.save(user);
        }

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
                .map(a -> userRepository.findById(a).orElseThrow(() -> new UserNotFoundException("users not found")))
                .collect(Collectors.toList());

        return users.stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserPhoneResponse retrievePhoneByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("찾으시는 계정이 존재하지 않습니다");
        }

        return UserPhoneResponse.builder()
                .phoneNumber(user.getPhone())
                .build();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

    }
}
