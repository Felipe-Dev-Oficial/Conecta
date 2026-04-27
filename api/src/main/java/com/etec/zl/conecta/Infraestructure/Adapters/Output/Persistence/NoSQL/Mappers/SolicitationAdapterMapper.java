package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.SolicitationEntity;
import org.springframework.stereotype.Component;

@Component
public class SolicitationAdapterMapper {

    public Solicitation toDomain(SolicitationEntity entity) {
        var nome = new Name(entity.getNome());
        var email = new Email(entity.getEmailSoliciter());
        return new Solicitation(
                entity.getId(),
                entity.getTypeRequirement(),
                entity.getOtherRequirement(),
                entity.isSolved(),
                entity.getIdSoliciter(),
                nome,
                email,
                entity.getIdCursos(),
                entity.getCreatedAt()
        );
    }
    public SolicitationEntity toEntity(Solicitation domain) {
        return new SolicitationEntity(
                domain.getId(),
                domain.getTypeRequirement(),
                domain.getOtherRequirement(),
                domain.isSolved(),
                domain.getIdSoliciter(),
                domain.getNome().name(),
                domain.getEmailSoliciter().email(),
                domain.getIdCursos(),
                domain.getCreatedAt()
        );
    }
}
