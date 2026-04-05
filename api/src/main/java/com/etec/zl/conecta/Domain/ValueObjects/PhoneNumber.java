package com.etec.zl.conecta.Domain.ValueObjects;


import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;

public record PhoneNumber(String number) {

    public PhoneNumber(String number) {
        if (number != null && number.matches("^[1-9]{2}[9][0-9]{8}$")) this.number = number;
        else throw new InvalidDataException("Celular invalido");
    }
}
