package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import org.springframework.stereotype.Component;

@Component
public class StatementAdapterMapper {

    public Statement toDomain(StatementEntity entity) {
        return new Statement(
                entity.getId(),
                entity.getIdSender(),
                entity.getTitle(),
                entity.getTimestamp(),
                entity.getContent(),
                entity.getMidia(),
                entity.getPriority(),
                entity.isEdited(),
                entity.getStatus(),
                entity.getTargetVO()
        );
    }
    public StatementEntity toEntity(Statement domain) {
        return new StatementEntity(
                domain.getId(),
                domain.getIdSender(),
                domain.getTitle(),
                domain.getTimestamp(),
                domain.getContent(),
                domain.getMidia(),
                domain.getPriority(),
                domain.isEdited(),
                domain.getStatus(),
                domain.getTargetVO()
        );
    }
}
