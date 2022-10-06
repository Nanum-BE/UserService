package com.nanum.userservice.user.application;

import com.nanum.exception.UserAlreadyExistException;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.jwt.JwtTokenProvider;
import com.nanum.utils.oauth.OAuthAttributes;
import com.nanum.utils.sms.vo.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final Environment env;
    private final RedisService redisService;

    @Value("${token.expiration_time}")
    private Long tokenValidTime;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("*********-*-*-*-*-*-*-*-");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        //kakao or naver
        String socialType = registrationId;

        log.info(String.valueOf(attributes));
        log.info(socialType);
        log.info(attributes.getEmail());
        log.info(String.valueOf(attributes.getRole()));

        User user = createSocialUser(attributes, socialType);

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    public User createSocialUser(OAuthAttributes attributes, String socialType) {

        log.info(env.getProperty("token.expiration_time"));
        User user;

        if (!userRepository.existsByEmail(attributes.getEmail())) {
//            user = userRepository.save(attributes.toEntity(socialType));
            user = redisService.createTemporalOAuthUser(attributes.getEmail(),
                    attributes.getNickname(),
                    attributes,
                    socialType);
        } else {
            throw new UserAlreadyExistException();
        }
        return user;
    }
}
