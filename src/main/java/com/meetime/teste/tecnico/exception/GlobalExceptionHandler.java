package com.meetime.teste.tecnico.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.Conflict.class)
    public ResponseEntity<Object> userAlreadExist(HttpClientErrorException ex) {
        return buildErrorResponse("THIS CONTACT ALREAD EXIST!", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<Object> hitTheMaxRequestLimit(HttpClientErrorException ex) {
        return buildErrorResponse("REQUEST LIMIT EXCEEDED!", HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(UserWithValidClientIdNotFoundException.class)
    public ResponseEntity<Object> userWithValidClientIdNotFound(UserWithValidClientIdNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> failedToReceiveAccessToken(FailedToReceiveAccessTokenException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> failedToRefreshAccessToken(FailedToRefreshAccessTokenException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}
