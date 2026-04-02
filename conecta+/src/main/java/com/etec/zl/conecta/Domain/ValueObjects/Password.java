package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Password(String password) {
    private static final Logger log = LoggerFactory.getLogger(Password.class);
    private static final String BASE_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
    private static final String BCRYPT_PATTERN = "^\\$2[aby]?\\$\\d{2}\\$.{53}$";

    public Password {
        if (password != null && password.matches(BCRYPT_PATTERN)) {
        } else if (password == null || !password.matches(BASE_PATTERN)) {
        InvalidDataException ex = new InvalidDataException("Password requirements not met");
        log.error("Validation failed for Password: ", ex);
        throw ex;
        }
    }
}
