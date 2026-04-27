package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities;

import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.TypeRequirement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "solicitations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitationEntity {

    @MongoId
    private UUID id;
    @Field("type_requirement")
    private TypeRequirement typeRequirement;
    @Field("other_requirement")
    private String otherRequirement;
    @Field("solved")
    private boolean solved;
    @Field("id_soliciter")
    @Indexed
    private String idSoliciter;
    @Field("name_soliciter")
    private String nome;
    @Field("email_soliciter")
    private String emailSoliciter;
    @Field("id_cursos")
    private List<String> idCursos;
    @Field("created_at")
    @Indexed(direction = IndexDirection.ASCENDING)
    private Instant createdAt;
}
