package com.ads.main.core.config.advice;


import com.ads.main.core.config.exception.AppException;
import com.ads.main.core.config.exception.NoAdException;
import com.ads.main.core.vo.RespVo;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpEntity handle404(NoHandlerFoundException e){
        return ResponseEntity.badRequest().body(new RespVo(e.getMessage()));
    }

    @ExceptionHandler({
        ValidationException.class,
        TypeMismatchException.class,
        IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpEntity<RespVo<String>> handleDefException(Exception e) {
        log.error("#### handleDefException", e);
        return ResponseEntity.badRequest().body(new RespVo(e.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespVo<List<String>> processValidationError(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        List<String> validErrors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {

            String builder = "[" +
                    fieldError.getField() +
                    "] => " +
                    fieldError.getDefaultMessage() +
                    " 입력된 값: [" +
                    fieldError.getRejectedValue() +
                    "]";

            validErrors.add(builder);
        }

        return new RespVo<>(validErrors);
    }

    @ExceptionHandler({
        AppException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpEntity<RespVo> handleAppException(Exception e) {
        return ResponseEntity.badRequest().body(new RespVo(e.getMessage()));
    }

    @ExceptionHandler({
        RuntimeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpEntity<RespVo> handleBaseException(Exception e) {
        log.error("#### handleDefException", e);

        return ResponseEntity.badRequest().body(new RespVo(ExceptionUtils.getStackTrace(e)));
    }

    @ExceptionHandler({
            NoAdException.class
    })
    public HttpEntity<RespVo> exception_201(Exception e) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new RespVo(e.getMessage()));
    }
    @ExceptionHandler({
            AccessDeniedException.class
    })
    public HttpEntity<RespVo> exception_403(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RespVo(e.getMessage()));
    }

//    @ExceptionHandler({
//        HttpRequestMethodNotSupportedException.class,
//    })
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public HttpEntity<RespVo> handleBaseException(Exception e) {
//        return ResponseEntity.badRequest().body(new RespVo(""));
//    }

}
