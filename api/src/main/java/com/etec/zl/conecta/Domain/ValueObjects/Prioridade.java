package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;

public enum Prioridade {

    URGENTE(3),
    ALTA(2),
    MEDIA(1),
    BAIXA(0);

    private final int peso;

    Prioridade(int peso) {
        this.peso = peso;
    }

    public Prioridade elevar() {
        return switch (this) {
            case BAIXA -> MEDIA;
            case MEDIA -> ALTA;
            case ALTA, URGENTE -> URGENTE;
        };
    }

    public Prioridade reduzir() {
        return switch (this) {
            case URGENTE -> ALTA;
            case ALTA -> MEDIA;
            case MEDIA, BAIXA -> BAIXA;
        };
    }

    public static Prioridade fromPeso(int peso) {
        return switch (peso) {
            case 3 -> URGENTE;
            case 2 -> ALTA;
            case 1 -> MEDIA;
            case 0 -> BAIXA;
            default -> throw new ProcessingErrorException("Peso de prioridade inválido: " + peso);
        };
    }

    public int getPeso() {
        return this.peso;
    }
}
