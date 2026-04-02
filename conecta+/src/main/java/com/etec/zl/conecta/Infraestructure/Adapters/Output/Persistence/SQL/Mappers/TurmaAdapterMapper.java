package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.TurmaEntity;
import org.springframework.stereotype.Component;

@Component
public class TurmaAdapterMapper {

    public Turma toDomain(TurmaEntity entity){
        return new Turma(
                entity.getId(),
                entity.getCurso(),
                entity.getModulos(),
                entity.getAtual(),
                entity.getStatus()
        );
    }
    public TurmaEntity toEntity(Turma domain){
        return new TurmaEntity(
                domain.getId(),
                domain.getCurso(),
                domain.getModulos(),
                domain.getAtual(),
                domain.getStatus()
        );
    }
}
