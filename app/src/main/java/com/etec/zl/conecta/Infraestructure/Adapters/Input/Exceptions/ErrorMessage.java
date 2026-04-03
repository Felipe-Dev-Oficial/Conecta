package com.etec.zl.conecta.Infraestructure.Adapters.Input.Exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorMessage {
    private LocalDateTime errorMoment;
    private String message;

    public ErrorMessage(String message) {
        this.errorMoment = LocalDateTime.now();
        this.message = message;
    }
}
