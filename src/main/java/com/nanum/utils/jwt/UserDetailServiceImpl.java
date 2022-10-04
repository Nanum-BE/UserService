package com.nanum.utils.jwt;

import com.nanum.exception.InformationDismatchException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, InformationDismatchException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new BadCredentialsException("이메일 혹은 비밀번호가 틀렸습니다");
        }

        return new UserDetailsImpl(user);
    }
}
