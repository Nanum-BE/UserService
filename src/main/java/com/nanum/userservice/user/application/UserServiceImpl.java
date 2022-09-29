package com.nanum.userservice.user.application;

import com.nanum.userservice.user.domain.UserEntity;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public void createUser(UserDto userDto) {

        userRepository.save(UserEntity.builder()
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
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return UserDto.builder()
                .userId(userEntity.getId())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .nickname(userEntity.getNickname())
                .pwd(userEntity.getPwd())
                .role(userEntity.getRole())
                .gender(userEntity.getGender())
                .phone(userEntity.getPhone())
                .build();
    }

    @Override
    public List<UserResponse> retrieveAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();

        List<UserResponse> userResponses = new ArrayList<>();

        userEntities.forEach(userEntity -> {
            userResponses.add(UserResponse.builder()
                    .email(userEntity.getEmail())
                    .name(userEntity.getName())
                    .build());
        });

        return userResponses;
    }

    @Override
    public UserResponse retrieveUser(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).get();

        return UserResponse.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null)
            throw new UsernameNotFoundException(username + ": not found");

        return new org.springframework.security.core.userdetails.User(userEntity.getEmail(), userEntity.getPwd(),
                true, true, true, true,
                new ArrayList<>());
    }
}
