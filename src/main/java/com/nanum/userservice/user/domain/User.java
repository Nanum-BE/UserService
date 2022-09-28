package com.nanum.userservice.user.domain;

import com.nanum.config.BaseTimeEntity;
import com.nanum.config.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Comment("사용자 실명")
    private String name;

    @Column(nullable = false)
    private String pwd;

    @Column(nullable = false, unique = true)
    @Comment("사용자 닉네임")
    private String nickname;

    @Comment("로그인 실패 횟수")
    private Integer failCnt;

    @Comment("프로필 사진 url")
    private String profileImgPath;

    @Comment("사용자면 USER, 호스트면 HOST")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String phone;

    @Comment("사용자의 성별, 남자면 1로 표시하고 여자면 0로 표시")
    @Column(nullable = false)
    private String gender;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
