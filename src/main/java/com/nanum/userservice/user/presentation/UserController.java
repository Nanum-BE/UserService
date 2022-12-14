package com.nanum.userservice.user.presentation;

import com.nanum.config.BaseResponse;
import com.nanum.exception.DuplicateEmailException;
import com.nanum.exception.DuplicateNickNameException;
import com.nanum.exception.ExceptionResponse;
import com.nanum.exception.PasswordDismatchException;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.domain.User;
import com.nanum.userservice.user.dto.UserDto;
import com.nanum.userservice.user.infrastructure.UserRepository;
import com.nanum.userservice.user.vo.*;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.ws.rs.Path;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "사용자", description = "사용자 관련 api")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "201", description = "created successfully",
                content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "bad request",
                content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "409", description = "conflict",
                content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
        @ApiResponse(responseCode = "500", description = "server error",
                content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
})

public class UserController {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UserService userService;
    private final UserRepository userRepository;


    @Operation(summary = "사용자 회원가입 API", description = "사용자가 회원가입을 하기 위한 요청")
    @PostMapping("/signup")
    public ResponseEntity<Object> createUser(@Valid @RequestPart UserRequest userRequest,
                                             @RequestPart(value = "profileImg", required = false) MultipartFile multipartFile) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(userRequest, UserDto.class);
        String result = "회원가입이 완료되었습니다";
        BaseResponse<String> response = new BaseResponse<>(result);

        Boolean user = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {
            user = userService.createUser(userDto, multipartFile);
        } else {
            userService.createUser(userDto, null);
        }

        String signFail = "다시 확인해주세요!";

        if (Boolean.FALSE.equals(user)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(signFail);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원가입시 이메일 중복검사 api", description = "이메일 중복검사를 하기 위한 요청")
    @GetMapping("/check/email/{email}")
    public ResponseEntity<BaseResponse<String>> checkEmail(@PathVariable String email) {
        String result = "사용 가능한 이메일입니다";
        BaseResponse<String> response = new BaseResponse<>(result);
        if (userService.checkEmail(email)) {
            throw new DuplicateEmailException();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @Operation(summary = "회원가입시 닉네임 중복검사 api", description = "닉네임 중복검사를 하기 위한 요청")
    @GetMapping("/check/nickname/{nickName}")
    public ResponseEntity<BaseResponse<String>> checkNickName(@PathVariable String nickName) {
        String result = "사용 가능한 닉네임입니다";
        BaseResponse<String> response = new BaseResponse<>(result);

        if (userService.checkNickName(nickName)) {
            throw new DuplicateNickNameException();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @Operation(summary = "전체 사용자 조회 api", description = "모든 사용자들의 정보를 조회하기 위한 요청")
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<List<UsersResponse>>> retrieveAllUsers() {
        List<UsersResponse> userResponses = userService.retrieveAllUsers();
        BaseResponse<List<UsersResponse>> responses = new BaseResponse<>(userResponses);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Operation(summary = "특정 사용자 정보 조회 api", description = "조회하고자 하는 특정 사용자의 정보 요청")
    @GetMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<UserResponse>> retrieveUser(@PathVariable Long userId) {

        UserResponse response = userService.retrieveUser(userId);
        BaseResponse<UserResponse> baseResponse = new BaseResponse<>(response);

        return ResponseEntity.status(HttpStatus.OK).body(baseResponse);
    }

    @GetMapping("/users/email/{email}")
    public UsersResponse retrieveUsers(@PathVariable String email) {
        return userService.retrieveUsers(email);
    }

    @Operation(summary = "사용자 정보수정", description = "수정된 정보로 다시 사용자 정보 수정하는 요청")
    @PutMapping("/users/{userId}")
    public ResponseEntity<BaseResponse<String>> modifyUser(@PathVariable Long userId,
                                                           @RequestPart UserModifyRequest request,
                                                           @RequestPart(value = "profileImg", required = false)
                                                                   MultipartFile file) {
        userService.modifyUser(userId, request, file);

        String result = "회원정보 수정이 완료되었습니다";
        BaseResponse<String> response = new BaseResponse<>(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "기존 비밀번화와 일치하는지", description = "비밀번호 변경전 기존 비밀번호가 일치하는지")
    @GetMapping("/check/{userId}/pwd/{pwd}")
    public ResponseEntity<BaseResponse<String>> checkPw(@PathVariable Long userId, @PathVariable String pwd) {

        User user = userRepository.findById(userId).get();
        String result = "기존 비밀번호와 일치합니다";
        BaseResponse<String> response = new BaseResponse<>(result);

        if (!bCryptPasswordEncoder.matches(pwd, user.getPwd())) {
            throw new PasswordDismatchException();
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경하기 위한 요청")
    @PutMapping("/users/{userId}/pw")
    public ResponseEntity<BaseResponse<String>> modifyUserPassword(@PathVariable Long userId,
                                                                   @RequestBody ModifyPasswordRequest passwordRequest) {
        userService.modifyUserPw(userId, passwordRequest);

        String result = "비밀번호 변경이 완료되었습니다";
        BaseResponse<String> response = new BaseResponse<>(result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "특정 전체 사용자 조회 api", description = "특정 모든 사용자들의 정보를 조회하기 위한 요청(ps id가 2이상일때만 사용가능)")
    @GetMapping("/users/particular")
    public ResponseEntity<BaseResponse<List<UsersResponse>>> retrieveUsersById(@RequestParam(value = "param",
            required = false, defaultValue = "") List<Long> params) {

        List<UsersResponse> userResponses = userService.retrieveUsersByUserIds(params);
        BaseResponse<List<UsersResponse>> responses = new BaseResponse<>(userResponses);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Operation(summary = "특정 전체 사용자 조회 api", description = "특정 모든 사용자들의 정보를 조회하기 위한 요청(ps id가 2이상일때만 사용가능)")
    @GetMapping("/users/particular/mono")
    public Mono<ResponseEntity<BaseResponse<List<UsersResponse>>>> retrieveUsersByIdMono(@RequestParam(value = "param",
            required = false, defaultValue = "") List<Long> params) {

        List<UsersResponse> userResponses = userService.retrieveUsersByUserIds(params);
        BaseResponse<List<UsersResponse>> responses = new BaseResponse<>(userResponses);
        Mono.just(responses);
        return Mono.just(responses)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/houses/users/lists/{userId}")
    public ResponseEntity<Object> retrieveUserInfoByIds(@PathVariable List<Long> userId) {
        List<UserResponse> responses = userService.retrieveUserInfoByIds(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(responses));
    }

    @GetMapping("/users/change/pw/email/{email}")
    public ResponseEntity<Object> retrievePhoneByEmail(@PathVariable String email) {
        UserPhoneResponse response = userService.retrievePhoneByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }
}
