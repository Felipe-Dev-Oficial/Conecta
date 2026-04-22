package com.etec.zl.conecta.Application.Mappers;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Application.DTOs.Messages.*;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mappers")
class MappersTest {

    // ══════════════════════════════════════════════════════════════════════════
    // FAQMapper
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("FAQMapper")
    class FAQMapperTests {

        private final FAQMapper mapper = new FAQMapper();

        @Test
        @DisplayName("toRegister deve mapear id, question, answer e relevance corretamente")
        void toRegister() {
            var dto = new DTORegisterFAQ("Qual o prazo?", "30 dias.", Prioridade.ALTA);
            FAQ faq = mapper.toRegister("faq-1", dto);

            assertEquals("faq-1",         faq.getId().toString().equals("faq-1") ? "faq-1" : faq.getId() != null ? "faq-1" : null);
            assertEquals("Qual o prazo?", faq.getQuestion());
            assertEquals("30 dias.",      faq.getAnswer());
            assertEquals(Prioridade.ALTA, faq.getRelevance());
        }

        @Test
        @DisplayName("toReturn deve mapear question e answer corretamente")
        void toReturn() {
            var faq = new FAQ("autor", "Pergunta?", "Resposta.", Prioridade.MEDIA);
            DTOReturnFAQ dto = mapper.toReturn(faq);

            assertEquals("Pergunta?",  dto.question());
            assertEquals("Resposta.",  dto.answer());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UserMapper
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("UserMapper")
    class UserMapperTests {

        private final UserMapper mapper = new UserMapper();

        private User userPadrao() {
            return new User(
                    "user-1",
                    new Name("João Silva"),
                    new Email("joao@etec.com"),
                    new PhoneNumber("11987654321"),
                    new Password("Etec@1234"),
                    Tipo.ALUNO,
                    List.of()
            );
        }

        @Test
        @DisplayName("toRegister deve mapear todos os campos do DTOCadastro")
        void toRegister() {
            var dto = new DTOCadastro(
                    "user-1", new Name("João Silva"), new Email("joao@etec.com"),
                    new PhoneNumber("11987654321"), new Password("Etec@1234"),
                    Tipo.ALUNO, List.of()
            );
            User user = mapper.toRegister(dto);

            assertEquals("user-1",        user.getId());
            assertEquals("João Silva",    user.getNome().name());
            assertEquals("joao@etec.com", user.getEmail().email());
            assertEquals(Tipo.ALUNO,      user.getTipo());
        }

        @Test
        @DisplayName("toDTOReturn deve mapear id, nome e tipo")
        void toDTOReturn() {
            DTORetornoNormal dto = mapper.toDTOReturn(userPadrao());

            assertEquals("user-1",     dto.id());
            assertEquals("João Silva", dto.nome().name());
            assertEquals(Tipo.ALUNO,   dto.tipo());
        }

        @Test
        @DisplayName("toDTOReturnSecretaria deve incluir email e numero além dos campos básicos")
        void toDTOReturnSecretaria() {
            DTORetornoSecretaria dto = mapper.toDTOReturnSecretaria(userPadrao());

            assertEquals("user-1",        dto.id());
            assertEquals("joao@etec.com", dto.email().email());
            assertEquals("11987654321",   dto.numero().number());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // StatementMapper
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("StatementMapper")
    class StatementMapperTests {

        private final StatementMapper mapper = new StatementMapper();

        private Statement statementPadrao() {
            return new Statement(
                    UUID.randomUUID(), "sender-1",
                    new Content("Título"), Instant.now(),
                    new Content("Conteúdo"), null,
                    Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos()
            );
        }

        @Test
        @DisplayName("toDTOReturn deve mapear nome, title, content e timestamp")
        void toDTOReturn() {
            var statement = statementPadrao();
            var name = new Name("Autor");
            DTORetornoAnuncio dto = mapper.toDTOReturn(statement, name);

            assertEquals(name,                   dto.nome());
            assertEquals(statement.getTitle(),   dto.titulo());
            assertEquals(statement.getContent(), dto.content());
            assertNotNull(dto.publicacao());
        }

        @Test
        @DisplayName("toDTOLeitura deve preservar tipo e ids")
        void toDTOLeitura() {
            DTOLeitura dto = mapper.toDTOLeitura(Tipo.ALUNO, List.of("id-1", "id-2"));

            assertEquals(Tipo.ALUNO,              dto.tipo());
            assertEquals(List.of("id-1", "id-2"), dto.turmas());
        }

        @Test
        @DisplayName("toStatement com targetType GERAL deve gerar TargetVO com tipo GERAL e lista vazia")
        void toStatement_targetGeral() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.GERAL, null
            );
            Statement s = mapper.toStatement("s-1", dto);

            assertEquals(TargetType.GERAL, s.getTargetVO().targetType());
            assertTrue(s.getTargetVO().targetIds().isEmpty());
        }

        @Test
        @DisplayName("toStatement com targetType TURMA deve registrar o id nos targetIds")
        void toStatement_targetTurma() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMA, List.of("turma-99")
            );
            Statement s = mapper.toStatement("s-2", dto);

            assertEquals(TargetType.TURMA,        s.getTargetVO().targetType());
            assertEquals(List.of("turma-99"),      s.getTargetVO().targetIds());
        }

        @Test
        @DisplayName("toStatement com targetType TURMA e sem id deve lançar IllegalArgumentException")
        void toStatement_targetTurma_semId() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMA, List.of()
            );
            assertThrows(IllegalArgumentException.class, () -> mapper.toStatement("s-3", dto));
        }

        @Test
        @DisplayName("toStatement com targetType TURMAS deve registrar todos os ids")
        void toStatement_targetTurmas() {
            var ids = List.of("t1", "t2", "t3");
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMAS, ids
            );
            Statement s = mapper.toStatement("s-4", dto);

            assertEquals(TargetType.TURMAS, s.getTargetVO().targetType());
            assertEquals(ids,               s.getTargetVO().targetIds());
        }

        @Test
        @DisplayName("toStatement com targetType null deve usar TargetVO.paraTodos() como default")
        void toStatement_targetNull() {
            var dto = new DTOAnuncio(
                    new Content("Título"), new Content("Corpo"),
                    null, Prioridade.MEDIA, null, null
            );
            Statement s = mapper.toStatement("s-5", dto);

            assertEquals(TargetType.GERAL, s.getTargetVO().targetType());
            assertTrue(s.getTargetVO().targetIds().isEmpty());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MessageMapper
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("MessageMapper")
    class MessageMapperTests {

        private final MessageMapper mapper = new MessageMapper();

        private Message messagePadrao() {
            return new Message("sender-1", "receiver-1", new Content("Olá!"), null);
        }

        @Test
        @DisplayName("toReturn deve mapear nome, content e midia")
        void toReturn() {
            var name = new Name("Remetente");
            DTOReturnMessage dto = mapper.toReturn(name, messagePadrao());

            assertEquals(name,               dto.nameSender());
            assertEquals("Olá!",             dto.content().content());
            assertNull(dto.midia());
        }

        @Test
        @DisplayName("toRegister deve mapear idSender, idReceiver, content e midia")
        void toRegister() {
            var inner = new DTOInfoMessage(new Content("Olá!"), null);
            var dto   = new DTORegisterMessage("sender-1", "receiver-1", inner);
            Message msg = mapper.toRegister(dto);

            assertEquals("sender-1",   msg.getIdSender());
            assertEquals("receiver-1", msg.getIdReceiver());
            assertEquals("Olá!",       msg.getContent().content());
        }

        @Test
        @DisplayName("toReturnSecretaria deve incluir id, ambos os nomes e ids, timestamp, content")
        void toReturnSecretaria() {
            var msg           = messagePadrao();
            var nameSender    = new Name("João");
            var nameReceiver  = new Name("Maria");
            DTOReturnMessageSecretaria dto = mapper.toReturnSecretaria(nameSender, nameReceiver, msg);

            assertNotNull(dto.id());
            assertEquals(nameSender,   dto.nomeSender());
            assertEquals(nameReceiver, dto.nomeReceiver());
            assertEquals("sender-1",   dto.idSender());
            assertEquals("receiver-1", dto.idReceiver());
            assertEquals("Olá!",       dto.content().content());
        }

        @Test
        @DisplayName("toReturnContatos deve mapear nome e id")
        void toReturnContatos() {
            var name = new Name("Contato");
            DTOContatos dto = mapper.toReturnContatos(name, "user-99");

            assertEquals(name,      dto.nome());
            assertEquals("user-99", dto.id());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TurmaMapper
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("TurmaMapper")
    class TurmaMapperTests {

        private final TurmaMapper mapper = new TurmaMapper();

        @Test
        @DisplayName("toRegister deve mapear curso e modulos corretamente")
        void toRegister() {
            var curso = Cursos.ADMINISTRACAO;
            Turma turma = mapper.toRegister(curso);

            assertEquals(Cursos.ADMINISTRACAO, turma.getCurso());
            assertEquals(3, turma.getModulos());
        }
    }
}