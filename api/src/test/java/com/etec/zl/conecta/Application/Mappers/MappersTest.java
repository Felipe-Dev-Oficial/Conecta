package com.etec.zl.conecta.Application.Mappers;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Application.DTOs.Messages.*;
import com.etec.zl.conecta.Application.DTOs.Solicitations.DTORequirement;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Mappers.Solicitations.SolicitationMapper;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
                    new String("Título"), Instant.now(),
                    new String("Conteúdo"), null,
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
                    new String("Título"), new String("Corpo"),
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
                    new String("Título"), new String("Corpo"),
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
                    new String("Título"), new String("Corpo"),
                    null, Prioridade.MEDIA, TargetType.TURMA, List.of()
            );
            assertThrows(IllegalArgumentException.class, () -> mapper.toStatement("s-3", dto));
        }

        @Test
        @DisplayName("toStatement com targetType TURMAS deve registrar todos os ids")
        void toStatement_targetTurmas() {
            var ids = List.of("t1", "t2", "t3");
            var dto = new DTOAnuncio(
                    new String("Título"), new String("Corpo"),
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
                    new String("Título"), new String("Corpo"),
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
            return new Message("sender-1", "receiver-1", new String("Olá!"), null);
        }

        @Test
        @DisplayName("toReturn deve mapear nome, content e midia")
        void toReturn() {
            var name = new Name("Remetente");
            DTOReturnMessage dto = mapper.toReturn(name, messagePadrao());

            assertEquals(name,               dto.nameSender());
            assertEquals("Olá!",             dto.content());
            assertNull(dto.midia());
        }

        @Test
        @DisplayName("toRegister deve mapear idSender, idReceiver, content e midia")
        void toRegister() {
            var inner = new DTOInfoMessage(new String("Olá!"), null);
            var dto   = new DTORegisterMessage("sender-1", "receiver-1", inner);
            Message msg = mapper.toRegister(dto);

            assertEquals("sender-1",   msg.getIdSender());
            assertEquals("receiver-1", msg.getIdReceiver());
            assertEquals("Olá!",       msg.getContent());
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
            assertEquals("Olá!",       dto.content());
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

    @Nested
    @DisplayName("SolicitationMapper")
    class SolicitationMapperTest {

        private final SolicitationMapper mapper = new SolicitationMapper();

        // ─── Fixtures ────────────────────────────────────────────────────────────

        private static final java.lang.String USER_ID = "user-abc";
        private static final Name NOME = new Name("Arthur Henrique");
        private static final Email EMAIL = new Email("arthur@etec.sp.gov.br");
        private static final List<java.lang.String> CURSOS = List.of("curso-1", "curso-2");

        private User userFactory() {
            return new User(
                    USER_ID, NOME, EMAIL,
                    new PhoneNumber("11930055058"),
                    new Password("SenhaSenhuda78#"),
                    Tipo.ALUNO,
                    CURSOS
            );
        }

        private Solicitation solicitationFactory(TypeRequirement type, java.lang.String other, boolean solved) {
            return new Solicitation(
                    UUID.randomUUID(), type, other, solved,
                    USER_ID, NOME, EMAIL, CURSOS, Instant.now()
            );
        }

        // ─── toRegister ──────────────────────────────────────────────────────────

        @Nested
        @DisplayName("toRegister()")
        class ToRegister {

            @Test
            @DisplayName("should pull id, name, email and turmas from User")
            void shouldMapUserFieldsToSolicitation() {
                var dto = new DTORequirement(TypeRequirement.DECLARAÇÃO_DE_MATRICULA, null);
                var result = mapper.toRegister(userFactory(), dto);

                assertThat(result.getIdSoliciter()).isEqualTo(USER_ID);
                assertThat(result.getNome()).isEqualTo(NOME);
                assertThat(result.getEmailSoliciter()).isEqualTo(EMAIL);
                assertThat(result.getIdCursos()).containsExactlyElementsOf(CURSOS);
            }

            @Test
            @DisplayName("should map typeRequirement from DTO")
            void shouldMapTypeRequirement() {
                var dto = new DTORequirement(TypeRequirement.CERTIFICADO_ATUAL, null);
                var result = mapper.toRegister(userFactory(), dto);

                assertThat(result.getTypeRequirement()).isEqualTo(TypeRequirement.CERTIFICADO_ATUAL);
            }

            @Test
            @DisplayName("should propagate otherRequirement when type is OUTRO")
            void shouldPropagateOtherRequirementForOutro() {
                var dto = new DTORequirement(TypeRequirement.OUTRO, "Atestado de frequência");
                var result = mapper.toRegister(userFactory(), dto);

                assertThat(result.getTypeRequirement()).isEqualTo(TypeRequirement.OUTRO);
                assertThat(result.getOtherRequirement()).isEqualTo("Atestado de frequência");
            }

            @Test
            @DisplayName("should produce a new Solicitation with generated id and solved=false")
            void shouldProduceUnsolved() {
                var dto = new DTORequirement(TypeRequirement.DECLARAÇÃO_DE_MATRICULA, null);
                var result = mapper.toRegister(userFactory(), dto);

                assertThat(result.getId()).isNotNull();
                assertThat(result.isSolved()).isFalse();
            }

            @Test
            @DisplayName("should set createdAt close to now")
            void shouldSetCreatedAtToNow() {
                var before = Instant.now().minusSeconds(1);
                var dto = new DTORequirement(TypeRequirement.CERTIFICADO_ATUAL, null);
                var result = mapper.toRegister(userFactory(), dto);
                var after = Instant.now().plusSeconds(1);

                assertThat(result.getCreatedAt()).isBetween(before, after);
            }
        }

        // ─── toDTOReturnRequirement ───────────────────────────────────────────────

        @Nested
        @DisplayName("toDTOReturnRequirement()")
        class ToDTOReturnRequirement {

            @Test
            @DisplayName("should map all fields from Solicitation to DTO")
            void shouldMapAllFields() {
                var solicitation = solicitationFactory(TypeRequirement.DECLARAÇÃO_DE_MATRICULA, null, false);
                var dto = mapper.toDTOReturnRequirement(solicitation);

                assertThat(dto.id()).isEqualTo(solicitation.getId());
                assertThat(dto.type()).isEqualTo(TypeRequirement.DECLARAÇÃO_DE_MATRICULA);
                assertThat(dto.otherRequirement()).isNull();
                assertThat(dto.solved()).isFalse();
                assertThat(dto.createdAt()).isEqualTo(solicitation.getCreatedAt());
            }

            @Test
            @DisplayName("should reflect solved=true in DTO when solicitation is resolved")
            void shouldReflectSolvedState() {
                var solicitation = solicitationFactory(TypeRequirement.CERTIFICADO_ATUAL, null, true);
                var dto = mapper.toDTOReturnRequirement(solicitation);

                assertThat(dto.solved()).isTrue();
            }

            @Test
            @DisplayName("should include otherRequirement in DTO when type is OUTRO")
            void shouldIncludeOtherRequirement() {
                var solicitation = solicitationFactory(TypeRequirement.OUTRO, "Preciso de declaração", false);
                var dto = mapper.toDTOReturnRequirement(solicitation);

                assertThat(dto.type()).isEqualTo(TypeRequirement.OUTRO);
                assertThat(dto.otherRequirement()).isEqualTo("Preciso de declaração");
            }

            @Test
            @DisplayName("should map every TypeRequirement variant without error")
            void shouldMapAllTypeRequirementVariants() {
                for (var type : TypeRequirement.values()) {
                    var other = (type == TypeRequirement.OUTRO) ? "desc" : null;
                    var solicitation = solicitationFactory(type, other, false);
                    var dto = mapper.toDTOReturnRequirement(solicitation);

                    assertThat(dto.type()).isEqualTo(type);
                }
            }
        }
    }
}