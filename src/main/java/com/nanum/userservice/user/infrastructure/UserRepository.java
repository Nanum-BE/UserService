package com.nanum.userservice.user.infrastructure;

import com.nanum.userservice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    User findByEmail(String username);

    @Transactional
    @Modifying
    @Query("update User u set u.warnCnt = u.warnCnt + 1 where u.id = :id")
    int replaceWarnCnt(Long id);

}
