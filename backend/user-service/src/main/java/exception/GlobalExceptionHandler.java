package com.velixa.user.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
	    ErrorResponse error = new ErrorResponse(
	            "FAILED",
	            HttpStatus.NOT_FOUND.value(),
	            ex.getMessage(),
	            LocalDateTime.now()
	    );
	    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}


	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
	    ErrorResponse error = new ErrorResponse(
	            "FAILED",
	            HttpStatus.BAD_REQUEST.value(),
	            ex.getMessage(),
	            LocalDateTime.now()
	    );
	    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}


//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
//        ErrorResponse error = new ErrorResponse(
//                LocalDateTime.now(),
//                HttpStatus.BAD_REQUEST.value(),
//                "Type Mismatch",
//                "Invalid value: " + ex.getValue()
//        );
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
