package com.ecommerce.productcart.exception;

import com.ecommerce.productcart.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<MessageResponse> handleNotFound(NoSuchElementException e) {
        return new ResponseEntity<>(new MessageResponse("Resource not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGeneral(Exception e) {
        return new ResponseEntity<>(new MessageResponse("Technical Error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
