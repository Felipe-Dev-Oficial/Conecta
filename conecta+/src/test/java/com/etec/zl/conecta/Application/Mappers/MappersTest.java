package com.etec.zl.conecta.Application.Mappers;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.Messages.DTOInfoMessage;
import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;
import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Application Mappers")
class MappersTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private User userPadrao() {
        return new User(
                "user-1",
                new Name("João Silva"),
                new Email("joao@etec.com"),
                new PhoneNumber("11987654321"),
                new Password("Etec@1234"),
                Tipo.ALUNO,
                new ArrayList<>()
        );
    }

    private FAQ faqPadrao() {
        return new FAQ(
                UUID.randomUUID(), "Qual o prazo?", "30 dias.",
                "autor-1", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.MEDIA
        );
    }

    private Message messagePadrao() {
        return new Message(
                UUID.randomUUID(), "sender-1", "receiver-1",
                Instant.now(), new Content("Olá!"), null
        );
    }

    private Statement statementPadrao() {
        return new Statement(
                UUID.randomUUID(), "sender-1",
                new Content("Título"), Instant.now(),
                new Content("Corpo"), null,
                Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos()
        );
    }

    // ─── UserMapper ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("UserMapper")
    class UserMapperTest {

        private final UserMapper mapper = new UserMapper();

        @Test
        @DisplayName("toDTOReturn() deve mapear id, nome e tipo")
        void toDTOReturn_mapeiaCorretamente() {
            var dto = mapper.toDTOReturn(userPadrao());
            assertEquals("user-1", dto.id());
            assertEquals("João Silva", dto.nome().name());
            assertEquals(Tipo.ALUNO, dto.tipo());
        }

        @Test
        @DisplayName("toDTOReturnSecretaria() deve mapear id, nome, email, numero e tipo")
        void toDTOReturnSecretaria_mapeiaCorretamente() {
            var dto = mapper.toDTOReturnSecretaria(userPadrao());
            assertEquals("user-1", dto.id());
            assertEquals("João Silva", dto.nome().name());
            assertEquals("joao@etec.com", dto.email().email());
            assertEquals("11987654321", dto.numero().number());
            assertEquals(Tipo.ALUNO, dto.tipo());
        }

        @Test
        @DisplayName("toRegister() deve criar User a partir do DTOCadastro")
        void toRegister_criaUserCorretamente() {
            var dto = new DTOCadastro(
                    "user-2",
                    new Name("Maria Lima"),
                    new Email("maria@etec.com"),
                    new PhoneNumber("11987654322"),
                    new Password("Etec@1234"),
                    Tipo.PROFESSOR,
                    new ArrayList<>()
            );
            var user = mapper.toRegister(dto);
            assertEquals("user-2", user.getId());
            assertEquals("Maria Lima", user.getNome().name());
            assertEquals("maria@etec.com", user.getEmail().email());
            assertEquals(Tipo.PROFESSOR, user.getTipo());
        }
    }

    // ─── FAQMapper ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("FAQMapper")
    class FAQMapperTest {

        private final FAQMapper mapper = new FAQMapper();

        @Test
        @DisplayName("toRegister() deve criar FAQ com os dados do DTO")
        void toRegister_criaFAQCorretamente() {
            var dto = new DTORegisterFAQ("Qual o prazo?", "30 dias.", Prioridade.ALTA);
            var faq = mapper.toRegister("autor-1", dto);
            assertEquals("autor-1", faq.getAuthorId());
            assertEquals("Qual o prazo?", faq.getQuestion());
            assertEquals("30 dias.", faq.getAnswer());
            assertEquals(Prioridade.ALTA, faq.getRelevance());
            assertEquals(StatusFAQ.RASCUNHO, faq.getStatusFAQ());
        }

        @Test
        @DisplayName("toReturn() deve mapear pergunta e resposta")
        void toReturn_mapeiaCorretamente() {
            var dto = mapper.toReturn(faqPadrao());
            assertEquals("Qual o prazo?", dto.question());
            assertEquals("30 dias.", dto.answer());
        }
    }
    // ─── MessageMapper ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MessageMapper")
    class MessageMapperTest {

        private final MessageMapper mapper = new MessageMapper();

        @Test
        @DisplayName("toRegister() deve criar Message a partir do DTO")
        void toRegister_criaMensagemCorretamente() {
            var dto = new DTORegisterMessage("sender-1", "receiver-1", new DTOInfoMessage(new Content("Olá!"), null));
            var message = mapper.toRegister(dto);
            assertEquals("sender-1", message.getIdSender());
            assertEquals("receiver-1", message.getIdReceiver());
            assertEquals("Olá!", message.getContent().content());
            assertNotNull(message.getId());
            assertNotNull(message.getTimestamp());
        }

        @Test
        @DisplayName("toReturn() deve mapear nome, conteúdo e mídia")
        void toReturn_mapeiaCorretamente() {
            var nome = new Name("João Silva");
            var dto = mapper.toReturn(nome, messagePadrao());
            assertEquals("João Silva", dto.nameSender().name());
            assertEquals("Olá!", dto.content().content());
            assertNull(dto.midia());
        }

        @Test
        @DisplayName("toReturnSecretaria() deve mapear todos os campos")
        void toReturnSecretaria_mapeiaCorretamente() {
            var msg = messagePadrao();
            var nomeSender = new Name("João Silva");
            var nomeReceiver = new Name("Maria Lima");
            var dto = mapper.toReturnSecretaria(nomeSender, nomeReceiver, msg);
            assertEquals(msg.getId(), dto.id());
            assertEquals("João Silva", dto.nomeSender().name());
            assertEquals("sender-1", dto.idSender());
            assertEquals("Maria Lima", dto.nomeReceiver().name());
            assertEquals("receiver-1", dto.idReceiver());
            assertNotNull(dto.timestamp());
            assertEquals("Olá!", dto.content().content());
        }

        @Test
        @DisplayName("toReturnContatos() deve mapear nome e id")
        void toReturnContatos_mapeiaCorretamente() {
            var nome = new Name("Pedro Costa");
            var dto = mapper.toReturnContatos(nome, "user-99");
            assertEquals("Pedro Costa", dto.nome().name());
            assertEquals("user-99", dto.id());
        }
    }

    // ─── StatementMapper ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("StatementMapper")
    class StatementMapperTest {

        private final StatementMapper mapper = new StatementMapper();

        @Test
        @DisplayName("toDTOReturn() deve mapear nome, título, timestamp e edited")
        void toDTOReturn_mapeiaCorretamente() {
            var nome = new Name("Ana Lima");
            var dto = mapper.toDTOReturn(statementPadrao(), nome);
            assertEquals("Ana Lima", dto.nome().name());
            assertEquals("Título", dto.titulo().content());
            assertNotNull(dto.publicacao());
            assertFalse(dto.edited());
        }

        @Test
        @DisplayName("toStatement() com targetType null deve usar TargetVO GERAL")
        void toStatement_targetTypeNull_usaGeral() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, null, null
            );
            var s = mapper.toStatement("sender-1", dto);
            assertEquals(TargetType.GERAL, s.getTargetVO().targetType());
        }

        @Test
        @DisplayName("toStatement() com targetType GERAL deve usar TargetVO GERAL")
        void toStatement_targetTypeGeral() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.GERAL, null
            );
            var s = mapper.toStatement("sender-1", dto);
            assertEquals(TargetType.GERAL, s.getTargetVO().targetType());
        }

        @Test
        @DisplayName("toStatement() com targetType PROFESSORES deve usar TargetVO PROFESSORES")
        void toStatement_targetTypeProfessores() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.PROFESSORES, null
            );
            var s = mapper.toStatement("sender-1", dto);
            assertEquals(TargetType.PROFESSORES, s.getTargetVO().targetType());
        }

        @Test
        @DisplayName("toStatement() com targetType TURMA e id válido deve criar TargetVO TURMA")
        void toStatement_targetTypeTurma_comId() {
            var id = UUID.randomUUID();
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMA, List.of(id)
            );
            var s = mapper.toStatement("sender-1", dto);
            assertEquals(TargetType.TURMA, s.getTargetVO().targetType());
            assertEquals(id, s.getTargetVO().targetIds().get(0));
        }

        @Test
        @DisplayName("toStatement() com targetType TURMA sem id deve lançar IllegalArgumentException")
        void toStatement_targetTypeTurma_semId_throwsIllegalArgument() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMA, null
            );
            assertThrows(IllegalArgumentException.class, () -> mapper.toStatement("sender-1", dto));
        }

        @Test
        @DisplayName("toStatement() com targetType TURMA e lista vazia deve lançar IllegalArgumentException")
        void toStatement_targetTypeTurma_listaVazia_throwsIllegalArgument() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMA, List.of()
            );
            assertThrows(IllegalArgumentException.class, () -> mapper.toStatement("sender-1", dto));
        }

        @Test
        @DisplayName("toStatement() com targetType TURMAS deve criar TargetVO com múltiplos ids")
        void toStatement_targetTypeTurmas() {
            var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMAS, ids
            );
            var s = mapper.toStatement("sender-1", dto);
            assertEquals(TargetType.TURMAS, s.getTargetVO().targetType());
            assertEquals(2, s.getTargetVO().targetIds().size());
        }

        @Test
        @DisplayName("toDTOLeitura() deve mapear tipo e ids corretamente")
        void toDTOLeitura_mapeiaCorretamente() {
            var ids = List.of(UUID.randomUUID());
            var dto = mapper.toDTOLeitura(Tipo.ALUNO, ids);
            assertEquals(Tipo.ALUNO, dto.tipo());
            assertEquals(ids, dto.turmas());
        }
    }

    // ─── TurmaMapper ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("TurmaMapper")
    class TurmaMapperTest {

        private final TurmaMapper mapper = new TurmaMapper();

        @Test
        @DisplayName("toRegister() deve criar Turma com curso e módulos do DTO")
        void toRegister_criaTurmaCorretamente() {
            var dto = new DTOCadastroTurma(Cursos.ADMINISTRACAO, 4);
            var turma = mapper.toRegister(dto);
            assertEquals(Cursos.ADMINISTRACAO, turma.getCurso());
            assertEquals(4, turma.getModulos());
            assertEquals(Status.ON, turma.getStatus());
            assertNotNull(turma.getId());
        }

        @Test
        @DisplayName("toRegister() deve gerar ids diferentes para DTOs iguais")
        void toRegister_idsUnicos() {
            var dto = new DTOCadastroTurma(Cursos.LOGISTICA, 3);
            var turma1 = mapper.toRegister(dto);
            var turma2 = mapper.toRegister(dto);
            assertNotEquals(turma1.getId(), turma2.getId());
        }
    }
}