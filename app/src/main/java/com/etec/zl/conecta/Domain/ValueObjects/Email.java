package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Email(String email) {

    private static final Logger log = LoggerFactory.getLogger(Email.class);

    public Email {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            InvalidDataException exception = new InvalidDataException("Invalid email format: " + email);
            log.error("Domain Validation Error: ", exception);
            throw exception;
        }
    }
}