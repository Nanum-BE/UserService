package com.nanum.userservice.user.application;

import com.nanum.exception.InformationDismatchException;
import com.nanum.exception.ProfileImgNotFoundException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.ModifyPasswordRequest;
import com.nanum.userservice.user.vo.UserModifyRequest;
import com.nanum.userservice.user.vo.UserResponse;
import com.nanum.utils.s3.S3UploaderService;
import com.nanum.utils.s3.dto.S3UploadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3UploaderService s3UploaderService;

    @Override
    public void createUser(UserDto userDto, MultipartFile multipartFile) {
        User user = userDto.userDtoToEntity();
        S3UploadDto s3UploadDto;

        if (multipartFile != null) {
            try {
                s3UploadDto = s3UploaderService.upload(multipartFile, "myspharosbucket", "userProfile");

                User.builder()
                        .email(userDto.getEmail())
                        .pwd(userDto.getPwd())
                        .nickname(userDto.getNickname())
                        .role(userDto.getRole())
                        .phone(userDto.getPhone())
                        .gender(userDto.getGender())
                        .isNoteReject(user.isNoteReject())
                        .profileImgPath(s3UploadDto.getImgUrl())
                        .saveName(s3UploadDto.getSaveName())
                        .originName(s3UploadDto.getOriginName())
                        .build();

            } catch (IOException e) {
                throw new ProfileImgNotFoundException();
            }
        }

        userRepository.save(user);
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

        userRepository.save(User.builder()
                .id(userId)
                .email(user.getEmail())
                .role(user.getRole())
                .loginFailCnt(user.getLoginFailCnt())
                .warnCnt(user.getWarnCnt())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .pwd(user.getPwd())
                .isNoteReject(request.isNoteReject())
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
                .isNoteReject(user.isNoteReject())
                .pwd(bCryptPasswordEncoder.encode(passwordRequest.getNewPw()))
                .build());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException(email);

        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .pwd(user.getPwd())
                .role(user.getRole())
                .gender(user.getGender())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public List<UserResponse> retrieveAllUsers() {
        List<User> userEntities = userRepository.findAll();

        List<UserResponse> userResponses = new ArrayList<>();

        userEntities.forEach(user -> {
            userResponses.add(UserResponse.builder()
                    .email(user.getEmail())
                    .nickName(user.getNickname())
                    .phone(user.getPhone())
                    .isNoteReject(user.isNoteReject())
                    .profileImgUrl(user.getProfileImgPath())
                    .gender(user.getGender())
                    .createAt(user.getCreateAt())
                    .build());
        });

        return userResponses;
    }

    @Override
    public UserResponse retrieveUser(Long userId) {
        User user = userRepository.findById(userId).get();

        return UserResponse.builder()
                .email(user.getEmail())
                .nickName(user.getNickname())
                .phone(user.getPhone())
                .isNoteReject(user.isNoteReject())
                .profileImgUrl(user.getProfileImgPath())
                .gender(user.getGender())
                .createAt(user.getCreateAt())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, InformationDismatchException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new InformationDismatchException();
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPwd(),
                true, true, true, true,
                new ArrayList<>());
    }
}
