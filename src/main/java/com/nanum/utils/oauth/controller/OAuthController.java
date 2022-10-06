package com.nanum.utils.oauth.controller;

import com.nanum.config.BaseResponse;
import com.nanum.userservice.user.application.CustomOAuth2UserService;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.application.UserServiceImpl;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.vo.UserRequest;
import com.nanum.utils.jwt.JwtTokenProvider;
import com.nanum.utils.oauth.vo.OAuthUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/oauth")
public class OAuthController {

    private final UserService userService;
    private final UserServiceImpl userServiceImpl;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<HashMap<Object, Object>>> retrieveUserInfo(@RequestBody OAuthUserRequest userRequest) {

        User user = userService.signOAuthUser(userRequest);
        String socialToken = jwtTokenProvider.createSocialToken(user.getId());

        HashMap<Object, Object> result = new HashMap<>();

        result.put("결과", "회원가입 완료");
        result.put("accessToken", socialToken);

        BaseResponse<HashMap<Object, Object>> response = new BaseResponse<>(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
