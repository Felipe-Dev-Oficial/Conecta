package com.etec.zl.conecta.Domain.Entities.Turmas;

import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.Status;

import java.time.LocalDate;

public class Turma {

    private String id;
    private Cursos curso;
    private Integer modulos;
    private Integer atual;
    private Status status;

    public Turma(String id, Cursos curso, Integer modulos, Integer atual, Status status) {
        this.id = id;
        this.curso = curso;
        this.modulos = modulos;
        this.atual = atual;
        this.status = status;
    }

    public Turma(Cursos curso, Integer modulos) {
        this.id = geradorDeId(curso);
        this.curso = curso;
        this.modulos = modulos;
        this.status = Status.ON;
        this.atual = 1;
    }

    public Turma() {
    }

    public void passarModulo(){
        if (this.modulos > this.atual) this.atual++;
        else this.status = this.status.desativar();
    }

    public String getId() {
        return id;
    }

    public Cursos getCurso() {
        return curso;
    }

    public Integer getModulos() {
        return modulos;
    }

    public Integer getAtual() {
        return atual;
    }

    public Status getStatus() {
        return status;
    }

    private String geradorDeId(Cursos curso){
        var sigla = curso.getSigla();
        String id = sigla + "-" + LocalDate.now().getYear();
        if (sigla.contains("MTEC") || sigla.contains("AMS")) {
            return id;
        } else {
            id += "-" + semestreAtual();
            return id;
        }
    }
    private int semestreAtual() {
        return (LocalDate.now().getMonthValue() <= 6) ? 1 : 2;
    }
}
