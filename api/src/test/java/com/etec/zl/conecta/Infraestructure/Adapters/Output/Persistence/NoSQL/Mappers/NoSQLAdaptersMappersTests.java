package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
import com.etec.zl.conecta.Domain.ValueObjects.StatusFAQ;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.MessageEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoSQLAdaptersMappersTest {

    private final FAQAdapterMapper faqMapper = new FAQAdapterMapper();
    private final MessageAdapterMapper msgMapper = new MessageAdapterMapper();
    private final StatementAdapterMapper stMapper = new StatementAdapterMapper();

    @Test
    void deveMapearFAQ() {
        FAQEntity entity = new FAQEntity(UUID.randomUUID(), "auth", "Q", "A", StatusFAQ.PUBLICADO, Instant.now(), null, Prioridade.ALTA);
        FAQ domain = faqMapper.toDomain(entity);
        assertEquals(entity.getQuestion(), domain.getQuestion());
        assertNotNull(faqMapper.toEntity(domain));
    }

    @Test
    void deveMapearMessage() {
        MessageEntity entity = new MessageEntity(UUID.randomUUID(), "S", "R", Instant.now(), new Content("Hi"), null);
        Message domain = msgMapper.toDomain(entity);
        assertEquals("Hi", domain.getContent().content());
    }

    @Test
    void deveMapearStatement() {
        StatementEntity entity = new StatementEntity();
        entity.setId(UUID.randomUUID());
        entity.setPriority(Prioridade.MEDIA);

        Statement domain = stMapper.toDomain(entity);
        assertEquals(Prioridade.MEDIA, domain.getPriority());
    }
}