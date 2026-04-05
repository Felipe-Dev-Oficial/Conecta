package com.etec.zl.conecta.Domain.Exceptions;

public class ValidationFailedException extends RuntimeException {
    public  ValidationFailedException() {super("Validation Failed");}

    public ValidationFailedException(String message) {
        super(message);
    }
}
