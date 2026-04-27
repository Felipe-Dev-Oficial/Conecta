package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.MessageEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.SolicitationEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoSQLAdaptersMappersTests {

    private final FAQAdapterMapper faqMapper = new FAQAdapterMapper();
    private final MessageAdapterMapper msgMapper = new MessageAdapterMapper();
    private final StatementAdapterMapper stMapper = new StatementAdapterMapper();
    private final SolicitationAdapterMapper solicitationMapper = new SolicitationAdapterMapper();

    @Test
    void deveMapearFAQ() {
        FAQEntity entity = new FAQEntity(UUID.randomUUID(), "auth", "Q", "A", StatusFAQ.PUBLICADO, Instant.now(), null, Prioridade.ALTA);
        FAQ domain = faqMapper.toDomain(entity);
        assertEquals(entity.getQuestion(), domain.getQuestion());
        assertNotNull(faqMapper.toEntity(domain));
    }

    @Test
    void deveMapearMessage() {
        MessageEntity entity = new MessageEntity(UUID.randomUUID(), "S", "R", Instant.now(), new String("Hi"), null);
        Message domain = msgMapper.toDomain(entity);
        assertEquals("Hi", domain.getContent());
    }

    @Test
    void deveMapearStatement() {
        StatementEntity entity = new StatementEntity();
        entity.setId(UUID.randomUUID());
        entity.setPriority(Prioridade.MEDIA.getPeso());
        Statement domain = stMapper.toDomain(entity);
        assertEquals(Prioridade.MEDIA, domain.getPriority());
    }

    @Test
    void deveMapearSolicitationEntityParaDomain() {
        UUID id = UUID.randomUUID();
        SolicitationEntity entity = new SolicitationEntity(id, TypeRequirement.OUTRO, "Descrição", false,
                "user-1", "João Silva", "joao@email.com", List.of("curso-1"), Instant.now());

        Solicitation domain = solicitationMapper.toDomain(entity);

        assertEquals(id, domain.getId());
        assertEquals("João Silva", domain.getNome().name());
        assertEquals("joao@email.com", domain.getEmailSoliciter().email());
        assertEquals(TypeRequirement.OUTRO, domain.getTypeRequirement());
        assertFalse(domain.isSolved());
    }

    @Test
    void deveMapearSolicitationDomainParaEntity() {
        Solicitation domain = new Solicitation(UUID.randomUUID(), TypeRequirement.OUTRO, "Descrição", false,
                "user-1", new Name("João Silva"), new Email("joao@email.com"), List.of("curso-1"), Instant.now());

        SolicitationEntity entity = solicitationMapper.toEntity(domain);

        assertEquals("João Silva", entity.getNome());
        assertEquals("joao@email.com", entity.getEmailSoliciter());
        assertEquals(domain.getId(), entity.getId());
    }
}