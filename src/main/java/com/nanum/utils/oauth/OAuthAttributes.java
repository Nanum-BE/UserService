package com.nanum.utils.oauth;

import com.nanum.config.Role;
import com.nanum.userservice.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String nickname;
    private String mobile;
    private String gender;
    private String email;
    private Role role;

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        log.info("-----------");
        log.info(String.valueOf(attributes));
        log.info(String.valueOf(attributes.get("profile")));
        return OAuthAttributes.builder()
                .email(String.valueOf(attributes.get("email")))
                .nickname(String.valueOf(attributes.get("name")))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .email(String.valueOf(response.get("email")))
                .mobile(String.valueOf(response.get("mobile")))
                .gender(String.valueOf(response.get("gender")))
                .nickname(String.valueOf(response.get("nickname")))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakao_profile = (Map<String, Object>) kakao_account.get("profile");
        log.info(String.valueOf(kakao_account));
        log.info(String.valueOf(kakao_profile));
        return OAuthAttributes.builder()
                .email(String.valueOf(kakao_account.get("email")))
                .nickname(String.valueOf(kakao_profile.get("nickname")))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity(String socialType) {
        return User.builder()
                .email(email)
                .phone(mobile)
                .nickname(nickname)
                .role(Role.USER)
                .gender(gender)
                .socialType(socialType)
                .build();
    }
}
