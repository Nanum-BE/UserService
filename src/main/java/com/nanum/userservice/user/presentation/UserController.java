package com.nanum.userservice.user.presentation;

import com.nanum.config.BaseResponse;
import com.nanum.exception.DuplicateEmailException;
import com.nanum.exception.DuplicateNickNameException;
import com.nanum.userservice.user.application.UserService;
import com.nanum.userservice.user.domain.User;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@RestController
@RequestMapping("/api")
@Tag(name = "사용자", description = "사용자 관련 api")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 회원가입 API", description = "사용자가 회원가입을 하기 위한 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created successfully",
                    content = @Content(schema = @Schema(defaultValue = "사용자 회원가입이 완료되었습니다."))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(defaultValue = "잘못된 입력 값입니다."))),
            @ApiResponse(responseCode = "500", description = "server error",
                    content = @Content(schema = @Schema(defaultValue = "서버 에러입니다."))),
    })
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

    @GetMapping("/signup/email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        if (userService.checkEmail(email)) {
            throw new DuplicateEmailException();
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다");
        }
    }

    @GetMapping("/signup/nickname/{nickName}")
    public ResponseEntity<?> checkNickName(@PathVariable String nickName) {

        if (userService.checkNickName(nickName)) {
            throw new DuplicateNickNameException();
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> retrieveAllUsers() {
        List<UserResponse> userResponses = userService.retrieveAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> retrieveUser(@PathVariable Long userId) {
        UserResponse response = userService.retrieveUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
