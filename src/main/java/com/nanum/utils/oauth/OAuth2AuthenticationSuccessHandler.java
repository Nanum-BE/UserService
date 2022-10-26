package com.nanum.utils.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nanum.config.BaseResponse;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.utils.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("-++--+-+-+-+-+-+-+-");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> kakao_account;
        Map<String, Object> kakao_profile;
        String email;
        String socialType;
        String nickName;

        log.info(String.valueOf(authentication));
        log.info(String.valueOf(authentication.getPrincipal()));
        log.info(String.valueOf(oAuth2User.getAttributes()));
        log.info(String.valueOf(authentication.getDetails()));


        if (oAuth2User.getAttributes().containsKey("kakao_account")) {
            socialType = "kakao";
            kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            kakao_profile = (Map<String, Object>) kakao_account.get("profile");
            email = String.valueOf(kakao_account.get("email"));
            log.info(kakao_profile.get("nickname").toString());
            nickName = String.valueOf(kakao_profile.get("nickname").toString());
            log.info(String.valueOf(kakao_account));
            log.info(String.valueOf(kakao_profile));
        } else {
            email = String.valueOf(oAuth2User.getAttributes().get("email"));
            nickName = String.valueOf(oAuth2User.getAttributes().get("nickname"));
            socialType = "kakao";
        }

        User user = userRepository.findByEmail(email);

        if (userRepository.existsByEmail(email)) {
            String socialToken = jwtTokenProvider.createSocialToken(user.getId());
            String url = makeRedirectUrl(socialToken, user.getId());
            response.addHeader("Authorization", socialToken);
            getRedirectStrategy().sendRedirect(request, response, url);
        }
        log.info(nickName);
        String url = sendInfoToRedirectUrl(email, nickName, socialType);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        getRedirectStrategy().sendRedirect(request, response, url);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//
//
//        Map<String, String> userInfo = new HashMap<>();
//
//        log.info(String.valueOf(kakao_account));
//        log.info(String.valueOf(kakao_profile));
//
//        userInfo.put("email", String.valueOf(kakao_account.get("email")));
//        userInfo.put("nickname", String.valueOf(kakao_profile.get("nickname")));
//        userInfo.put("socialType", socialType);
//
//        BaseResponse<Map<String, String>> baseResponse = new BaseResponse<>(userInfo);
//
//        mapper.writeValue(response.getOutputStream(), baseResponse);
    }

    private String makeRedirectUrl(String token, Long userId) {
        return UriComponentsBuilder.fromUriString("http://3.37.166.100:8000/login" + token + "/" + userId)
                .build().toUriString();
    }

    private String sendInfoToRedirectUrl(String email, String nickName, String socialType) {
        String e = "/email=";
        String n = "/nickname=";
        String s = "/socialType=";

        return UriComponentsBuilder.fromUriString("http://3.37.166.100:8000/login/oauth2/code/kakao" + e + email + n + URLDecoder.decode(nickName) + s + socialType)
                .build().toUriString();
    }


}
