package com.nanum.userservice.user.infrastructure;

import com.nanum.userservice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickName);

    User findByEmail(String username);

    boolean existsByPhone(String phone);

    User findByPhone(String phone);

    boolean existsByPhoneAndEmail(String phone, String email);

    @Transactional
    @Modifying
    @Query("update User u set u.warnCnt = u.warnCnt + 1 where u.id = :id")
    int replaceWarnCnt(Long id);

}
