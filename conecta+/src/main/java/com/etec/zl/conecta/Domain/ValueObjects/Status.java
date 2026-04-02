package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;

public enum Status {

    ON,
    OFF;

    public Status desativar(){
        if(this.equals(ON)) return OFF;
        else throw new InvalidDataException("turma já apagada");
    }
}
