package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;

public record Content(String content) {

    public Content {
        if (content == null || content.isEmpty()) throw new InvalidDataException("Content cannot be empty");
    }
}
