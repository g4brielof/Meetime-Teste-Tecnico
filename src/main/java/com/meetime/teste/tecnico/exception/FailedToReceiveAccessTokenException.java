package com.meetime.teste.tecnico.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FailedToReceiveAccessTokenException extends RuntimeException {
    public FailedToReceiveAccessTokenException(String message) {
        super(message);
    }
}
