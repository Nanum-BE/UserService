package com.nanum.userservice.user.application;

import com.nanum.exception.InformationDismatchException;
import com.nanum.exception.PasswordDismatchException;
import com.nanum.exception.UserNotFoundException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.ModifyPasswordRequest;
import com.nanum.userservice.user.vo.UserModifyRequest;
import com.nanum.userservice.user.vo.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void createUser(UserDto userDto) {

        userRepository.save(User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .pwd(bCryptPasswordEncoder.encode(userDto.getPwd()))
                .nickname(userDto.getNickname())
                .role(userDto.getRole())
                .phone(userDto.getPhone())
                .gender(userDto.getGender())
                .isNoteReject(userDto.isNoteReject())
                .build());
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
                .name(request.getName())
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
                    .name(user.getName())
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
                .name(user.getName())
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
                    .name(user.getName())
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

    // 박찬흠 수정함
    @Override
    public UserResponse retrieveUser(Long userId) {

        Optional<User> userEntity = userRepository.findById(userId);
        if(userEntity.isEmpty()){
            throw new UserNotFoundException(String.format("ID[%s] not found",userId));
        }
//        User user = userRepository.findById(userId).get();
        User user = userEntity.get();

        return UserResponse.builder()
                .name(user.getName())
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
    public List<UserResponse> retrieveUsersByUserIds(List Longs) {
        List<User> users = userRepository.findAllById(Longs);
        if(users.size()<1){
            throw new UserNotFoundException(String.format("users[%s] not found", Longs));
        }

        List<UserResponse> userResponses = new ArrayList<>();

        users.forEach(user -> {
            userResponses.add(UserResponse.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickName(user.getNickname())
                    .phone(user.getPhone())
                    .isNoteReject(user.isNoteReject())
                    .profileImgUrl(user.getProfileImgPath())
                    .gender(user.getGender())
                    .createAt(user.getCreateAt())
                    .userId(user.getId())
                    .build());
        });

        return userResponses;
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
