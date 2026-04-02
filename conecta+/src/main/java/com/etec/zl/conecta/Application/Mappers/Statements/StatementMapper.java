package com.etec.zl.conecta.Application.Mappers.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.TargetVO;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;

import java.util.List;
import java.util.UUID;

public class StatementMapper {

    public DTORetornoAnuncio toDTOReturn(Statement statement, Name name) {
        return new DTORetornoAnuncio(
                name,
                statement.getTitle(),
                statement.getContent(),
                statement.getTimestamp(),
                statement.isEdited()
        );
    }

    public DTOLeitura toDTOLeitura(Tipo tipo, List<UUID> ids){
        return new DTOLeitura(tipo, ids);
    }

    public Statement toStatement(String id, DTOAnuncio dto) {
        return new Statement(
                id,
                dto.title(),
                dto.content(),
                dto.midia(),
                dto.priority(),
                orquestrarTarget(dto)
        );
    }
    private TargetVO orquestrarTarget(DTOAnuncio dto) {
        if (dto.targetType() == null) {
            return TargetVO.paraTodos();
        }
        return switch (dto.targetType()) {
            case GERAL -> TargetVO.paraTodos();
            case PROFESSORES -> TargetVO.paraProfessores();
            case TURMA -> {
                if (dto.targetsId() == null || dto.targetsId().isEmpty()) {
                    throw new IllegalArgumentException("ID da turma é obrigatório para este tipo de alvo.");
                }
                yield TargetVO.paraTurma(dto.targetsId().get(0));
            }
            case TURMAS -> TargetVO.paraListaDeTurmas(dto.targetsId());
            default -> TargetVO.paraTodos();
        };
    }
}
