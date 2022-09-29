package com.nanum.userservice.user.presentation;

import com.nanum.config.BaseResponse;
import com.nanum.exception.DuplicateEmailException;
import com.nanum.exception.DuplicateNickNameException;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.vo.UserRequest;
import com.nanum.userservice.user.vo.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "사용자", description = "사용자 관련 api")
@Slf4j
@RequiredArgsConstructor
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
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 회원가입 API", description = "사용자가 회원가입을 하기 위한 요청")
    @PostMapping("/signup")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest userRequest) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(userRequest, UserDto.class);

        String result = "회원가입이 완료되었습니다";
        BaseResponse<String> response = new BaseResponse<>(result);

        userService.createUser(userDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원가입시 이메일 중복검사 api", description = "이메일 중복검사를 하기 위한 요청")
    @GetMapping("/signup/email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        if (userService.checkEmail(email)) {
            throw new DuplicateEmailException();
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다");
        }
    }
    
    @Operation(summary = "회원가입시 닉네임 중복검사 api", description = "닉네임 중복검사를 하기 위한 요청")
    @GetMapping("/signup/nickname/{nickName}")
    public ResponseEntity<?> checkNickName(@PathVariable String nickName) {

        if (userService.checkNickName(nickName)) {
            throw new DuplicateNickNameException();
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다");
        }
    }

    @Operation(summary = "전체 사용자 조회 api", description = "모든 사용자들의 정보를 조회하기 위한 요청")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> retrieveAllUsers() {
        List<UserResponse> userResponses = userService.retrieveAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @Operation(summary = "특정 사용자 정보 조회 api", description = "조회하고자 하는 특정 사용자의 정보 요청")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> retrieveUser(@PathVariable Long userId) {
        UserResponse response = userService.retrieveUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
