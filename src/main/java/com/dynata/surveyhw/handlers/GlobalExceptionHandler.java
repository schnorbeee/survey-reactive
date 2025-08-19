package com.dynata.surveyhw.handlers;

import com.dynata.surveyhw.handlers.responses.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return Mono.just(ResponseEntity.status(status)
                .body(build(status, e)));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleRuntimeException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return Mono.just(ResponseEntity.status(status)
                .body(build(status, e)));
    }

    private ExceptionResponse build(HttpStatus statusCode, Throwable throwable) {
        return ExceptionResponse.builder()
                .statusCode(statusCode)
                .message(throwable.getMessage())
                .detail("Location: " + throwable.getLocalizedMessage())
                .build();
    }
}
