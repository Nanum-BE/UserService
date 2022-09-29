package com.nanum.userservice.user.application;

import com.nanum.exception.PasswordDismatchException;
import com.nanum.userservice.user.domain.User;
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

    @Override
    public UserResponse retrieveUser(Long userId) {
        User user = userRepository.findById(userId).get();

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, PasswordDismatchException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new PasswordDismatchException();
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPwd(),
                true, true, true, true,
                new ArrayList<>());
    }
}
