package com.nanum.userservice.user.infrastructure;

import com.nanum.userservice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    User findByEmail(String username);

}
