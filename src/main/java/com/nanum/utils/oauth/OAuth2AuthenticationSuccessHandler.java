package com.nanum.utils.oauth;

import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.infrastructure.UserRepository;
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
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> kakao_account;
        String email;

        if (oAuth2User.getAttributes().containsKey("kakao_account")) {
            kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = String.valueOf(kakao_account.get("email"));
        } else {
            email = String.valueOf(oAuth2User.getAttributes().get("email"));
        }

        User user = userRepository.findByEmail(email);

        if (response.isCommitted()) {
//            logger.debug("응답이 이미 커밋된 상태입니다. " + url + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }
//        getRedirectStrategy().sendRedirect(request, response, url);

    }

    private String makeRedirectUrl(String token) {
        return UriComponentsBuilder.fromUriString("http://13.209.26.150:9000/comm-users/social/" + token)
                .build().toUriString();
    }


}
