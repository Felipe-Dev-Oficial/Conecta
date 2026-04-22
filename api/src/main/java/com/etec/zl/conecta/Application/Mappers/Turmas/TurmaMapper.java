package com.etec.zl.conecta.Application.Mappers.Turmas;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;

public class TurmaMapper {

    public Turma toRegister(Cursos cursos) {
        return new Turma(
                cursos,
                cursos.getModulosTotais()
        );
    }
}
