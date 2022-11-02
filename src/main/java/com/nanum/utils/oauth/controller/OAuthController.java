package com.nanum.utils.oauth.controller;

import com.nanum.config.BaseResponse;
import com.nanum.config.Role;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.application.UserServiceImpl;
import com.nanum.userservice.user.domain.User;
import com.nanum.utils.jwt.JwtTokenProvider;
import com.nanum.utils.oauth.vo.OAuthUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "소셜로그인", description = "소셜로그인 추가 정보 입력받은 후 회원가입 시키기 위한 api")
@RequestMapping("/api/v1/oauth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "201", description = "created successfully",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "bad request",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "500", description = "server error",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
})
public class OAuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "소셜 회원가입 요청 api", description = "추가 정보들 입력한 후 회원가입 요청")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<HashMap<Object, Object>>> retrieveUserInfo(@Valid @RequestPart OAuthUserRequest userRequest
            , @RequestPart(value = "profileImg", required = false) MultipartFile multipartFile) {
        User user;

        if (multipartFile != null && !multipartFile.isEmpty()) {
            user = userService.signOAuthUser(userRequest, multipartFile);
        } else {
            user = userService.signOAuthUser(userRequest, null);
        }

        String socialToken = jwtTokenProvider.createSocialToken(user.getId());
        HashMap<Object, Object> result = new HashMap<>();

        result.put("결과", "회원가입 완료");
        result.put("accessToken", socialToken);

        BaseResponse<HashMap<Object, Object>> response = new BaseResponse<>(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/social/userId={userId}/role={role}/token={token}")
    public String socialSuccess(@PathVariable Long userId, @PathVariable Role role, @PathVariable String token) {

        return UriComponentsBuilder.fromUriString("https://nanum.site/login/oauth2/code/social" + "/userId=" + userId
                + "/role=" + role + "/token=" + token).toUriString();
    }
}
