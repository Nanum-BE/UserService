package com.nanum.userservice.user.infrastructure;

import com.nanum.userservice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    User findByEmail(String username);
}
