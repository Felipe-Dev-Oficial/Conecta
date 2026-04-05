package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public record TokenUpdater(String token, LocalDateTime expiration) {
    private static final Logger log = LoggerFactory.getLogger(TokenUpdater.class);

    public static TokenUpdater Start() {
        return new TokenUpdater(UUID.randomUUID().toString(), LocalDateTime.now().plusHours(1));
    }

    public void Check(String inputToken) {
        if (!this.token.equals(inputToken)) {
            ValidationFailedException ex = new ValidationFailedException("Invalid token");
            log.error("Security: Invalid token", ex);
            throw ex;
        }
        if (LocalDateTime.now().isAfter(this.expiration)) {
            ValidationFailedException ex = new ValidationFailedException("Token expired");
            log.error("Security: Expired token", ex);
            throw ex;
        }
    }
}