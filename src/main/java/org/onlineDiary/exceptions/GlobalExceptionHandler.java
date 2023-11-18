package org.onlineDiary.exceptions;

import jakarta.persistence.NonUniqueResultException;
import lombok.extern.slf4j.Slf4j;
import org.onlineDiary.dto.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ResponseError> catchIllegalArgsException(IllegalArgsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ResponseError(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }
    @ExceptionHandler
    public ResponseEntity<ResponseError> catchNonUniqueResultException(NonUniqueResultException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ResponseError(e.getMessage()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<ResponseError> catchResourceNotFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ResponseError(e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
