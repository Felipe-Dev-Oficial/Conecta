package com.etec.zl.conecta.Domain.Exceptions;

public class ProcessingErrorException extends RuntimeException {
    public ProcessingErrorException() {super("Internal error");}

    public ProcessingErrorException(String message) {
        super(message);
    }
}
