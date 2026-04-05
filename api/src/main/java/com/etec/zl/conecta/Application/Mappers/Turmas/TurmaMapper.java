package com.etec.zl.conecta.Application.Mappers.Turmas;

import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;

public class TurmaMapper {

    public Turma toRegister(DTOCadastroTurma dto) {
        return new Turma(
                dto.curso(),
                dto.modulos()
        );
    }
}
