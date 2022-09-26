package com.nanum.userservice.user.domain;

import com.nanum.config.BaseTimeEntity;
import com.nanum.config.Role;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    @Comment("사용자 실명")
    private String name;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false, unique = true)
    @Comment("사용자 닉네임")
    private String nickname;

    @Comment("프로필 사진 url")
    private String profileImgPath;

    @Comment("사용자면 USER, 호스트면 HOST")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String phone;


}
