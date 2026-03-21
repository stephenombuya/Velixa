package com.velixa.workspaceservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VelixaException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public VelixaException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static VelixaException notFound(String message) {
        return new VelixaException(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }

    public static VelixaException forbidden(String message) {
        return new VelixaException(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public static VelixaException badRequest(String message) {
        return new VelixaException(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public static VelixaException conflict(String message) {
        return new VelixaException(message, HttpStatus.CONFLICT, "CONFLICT");
    }
}
