package com.nanum.userservice.user.application;

import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.oauth.OAuthAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        String socialType = registrationId;

        User user = createSocialUser(attributes, socialType);
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())), attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private User createSocialUser(OAuthAttributes attributes, String socialType) {
        User user = null;
        if (!userRepository.existsByEmail(attributes.getEmail())) {
            user = userRepository.save(attributes.toEntity(socialType));
        }
        return user;
    }
}
