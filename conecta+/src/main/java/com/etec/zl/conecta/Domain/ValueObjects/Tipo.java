package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;

public enum Tipo {

    ALUNO,
    PROFESSOR,
    SECRETARIA,
    DESATIVADO;

    public Tipo desativar() {
        if (this == DESATIVADO) {
            throw new InvalidDataException("A conta já está desativada.");
        }
        return DESATIVADO;
    }

    public Tipo mudarPara(Tipo novoTipo) {
        if (novoTipo == null) {
            throw new InvalidDataException("O novo tipo não pode ser nulo.");
        }

        if (this == novoTipo) {
            throw new InvalidDataException("O usuário já possui o tipo: " + this.name());
        }

        return novoTipo;
    }
}
