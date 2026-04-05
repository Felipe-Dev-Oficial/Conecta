package com.etec.zl.conecta.Infraestructure.Adapters.Input.Exceptions;

import com.etec.zl.conecta.Domain.Exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorMessage> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("Error: ", ex.getMessage(), ex);
        return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(InvalidDataException.class)
    private ResponseEntity<ErrorMessage> handleInvalidDataException(InvalidDataException ex) {
        log.error("Error: ", ex.getMessage(), ex);
        return ResponseEntity.status(400).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(ValidationFailedException.class)
    private ResponseEntity<ErrorMessage> handleValidationFailedException(ValidationFailedException ex) {
        log.error("Error: ", ex.getMessage(), ex);
        return ResponseEntity.status(401).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(ProcessingErrorException.class)
    private ResponseEntity<ErrorMessage> handleProcessingErrorException(ProcessingErrorException ex) {
        log.error("Error: ", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    private ResponseEntity<ErrorMessage> handleAccessDeniedException(Exception ex) {
        log.error("Security Error: ", ex.getMessage(), ex);
        return ResponseEntity.status(403).body(new ErrorMessage("Access denied"));
    }
}