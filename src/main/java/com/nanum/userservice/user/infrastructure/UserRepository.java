package com.nanum.userservice.user.infrastructure;

import com.nanum.userservice.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    UserEntity findByEmail(String username);
}
