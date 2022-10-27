package com.nanum.utils.oauth;

import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> kakao_account;
        Map<String, Object> kakao_profile;
        String email;
        String socialType;
        String nickName;

        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());
        if (oAuth2User.getAttributes().containsKey("kakao_account")) {
            socialType = "kakao";
            kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            kakao_profile = (Map<String, Object>) kakao_account.get("profile");
            email = String.valueOf(kakao_account.get("email"));
            nickName = String.valueOf(kakao_profile.get("nickname").toString());
        } else {
            log.info(String.valueOf(oAuth2User.getAttributes().keySet()));
            email = String.valueOf(oAuth2User.getAttributes().get("email"));
            nickName = String.valueOf(oAuth2User.getAttributes().get("name"));
            socialType = "google";
        }

        User user = userRepository.findByEmail(email);
        String url;
        if (userRepository.existsByEmail(email)) {
            String socialToken = jwtTokenProvider.createSocialToken(user.getId());
            url = makeRedirectUrl(socialToken, user.getId());
            response.addHeader("Authorization", socialToken);
        } else {
            System.out.println("nickName = " + nickName);
            System.out.println("socialType = " + socialType);
            url = sendInfoToRedirectUrl(email, nickName, socialType);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    private String makeRedirectUrl(String token, Long userId) {
        String t = "/";
        return UriComponentsBuilder.fromUriString("https://nanum.site/user-service/api/v1/oauth/social" + t + token + "/" + userId)
                .build().toUriString();
    }

    private String sendInfoToRedirectUrl(String email, String nickName, String socialType) throws UnsupportedEncodingException {
        String e = "/email=";
        String n = "/nickname=";
        String s = "/socialType=";
        String encode = URLEncoder.encode(nickName, StandardCharsets.UTF_8);

        return UriComponentsBuilder.fromUriString("https://nanum.site/login/oauth2/code/social" + e + email + n + encode + s + socialType)
                .build().toUriString();
    }


}
