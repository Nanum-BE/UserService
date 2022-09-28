package com.nanum.exception;

import com.nanum.config.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    // 400 에러
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            RuntimeException.class,
            ConstraintViolationException.class,
            DuplicateKeyException.class
    })

    public ResponseEntity<Object> BadRequestException(final RuntimeException ex) {
        log.warn("400 error", ex);

        BaseResponse<String> response = new BaseResponse<>("잘못된 입력 값입니다.", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }

    public ResponseEntity<Object> ConstraintViolationException(final ConstraintViolationException ex) {
        log.warn("400에러");

        BaseResponse<String> response = new BaseResponse<>("이미 존재하는 --입니다.", ex.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
    // 401 에러
//    @ExceptionHandler({Exception.class})
//    public ResponseEntity<Object> DuplicateKeyException(final DuplicateKeyException e) {
//        log.warn("401에러");
//
//        BaseResponse<String> response
//    }
    // 500 에러
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(final Exception ex) {
        log.info(ex.getClass().getName());
        log.error("500 error", ex);

        BaseResponse<String> response = new BaseResponse<>("서버 에러입니다.", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}