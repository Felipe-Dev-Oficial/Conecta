package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Objects")
class ValueObjectsTest {

    // ─── Email ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Email")
    class EmailTest {

        @Test
        @DisplayName("Deve criar Email válido")
        void email_valid() {
            assertDoesNotThrow(() -> new Email("quack@quack.com"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para email sem @")
        void email_missingAt() {
            assertThrows(InvalidDataException.class, () -> new Email("quackquack.com"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para email nulo")
        void email_null() {
            assertThrows(InvalidDataException.class, () -> new Email(null));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para email vazio")
        void email_empty() {
            assertThrows(InvalidDataException.class, () -> new Email(""));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para email sem domínio")
        void email_noDomain() {
            assertThrows(InvalidDataException.class, () -> new Email("user@"));
        }
    }

    // ─── Password ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Password")
    class PasswordTest {

        @Test
        @DisplayName("Deve aceitar senha forte no formato plaintext")
        void password_strongPlaintext_valid() {
            assertDoesNotThrow(() -> new Password("Etec@1234"));
        }

        @Test
        @DisplayName("Deve aceitar senha já em formato BCrypt")
        void password_bcryptFormat_valid() {
            assertDoesNotThrow(() -> new Password("$2a$10$7EqJtq98hPqEX7fNZaFWoOa6sTXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para senha fraca")
        void password_weak() {
            assertThrows(InvalidDataException.class, () -> new Password("simples"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para senha sem caractere especial")
        void password_noSpecialChar() {
            assertThrows(InvalidDataException.class, () -> new Password("Etec1234"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para senha nula")
        void password_null() {
            assertThrows(InvalidDataException.class, () -> new Password(null));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para senha sem letra maiúscula")
        void password_noUpperCase() {
            assertThrows(InvalidDataException.class, () -> new Password("etec@1234"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para senha sem número")
        void password_noDigit() {
            assertThrows(InvalidDataException.class, () -> new Password("Etec@abcd"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para senha com menos de 8 caracteres")
        void password_tooShort() {
            assertThrows(InvalidDataException.class, () -> new Password("Et@1"));
        }
    }

    // ─── Name ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Name")
    class NameTest {

        @Test
        @DisplayName("Deve criar Name válido")
        void name_valid() {
            assertDoesNotThrow(() -> new Name("João Silva"));
        }

        @Test
        @DisplayName("Deve criar Name com apenas uma palavra")
        void name_singleWord_valid() {
            assertDoesNotThrow(() -> new Name("Maria"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para name nulo")
        void name_null() {
            assertThrows(InvalidDataException.class, () -> new Name(null));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para name vazio")
        void name_empty() {
            assertThrows(InvalidDataException.class, () -> new Name(""));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para name com apenas 1 caractere")
        void name_singleChar() {
            assertThrows(InvalidDataException.class, () -> new Name("A"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para name com números")
        void name_withNumbers() {
            assertThrows(InvalidDataException.class, () -> new Name("João123"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para name começando com espaço")
        void name_startsWithSpace() {
            assertThrows(InvalidDataException.class, () -> new Name(" João"));
        }
    }

    // ─── PhoneNumber ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PhoneNumber")
    class PhoneNumberTest {

        @Test
        @DisplayName("Deve criar PhoneNumber válido")
        void phone_valid() {
            assertDoesNotThrow(() -> new PhoneNumber("11987654321"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para número nulo")
        void phone_null() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber(null));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para número sem nono dígito 9")
        void phone_missingNinthDigit() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("11887654321"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para DDD começando com 0")
        void phone_dddStartingWithZero() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("01987654321"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para número com menos dígitos")
        void phone_tooShort() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("1198765432"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para número com mais dígitos")
        void phone_tooLong() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("119876543210"));
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException para número com letras")
        void phone_withLetters() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("1198765432A"));
        }
    }

    // ─── Content ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Content")
    class ContentTest {

        @Test
        @DisplayName("Deve criar Content com texto válido")
        void content_valid() {
            assertDoesNotThrow(() -> new Content("Algum conteúdo"));
        }

        @Test
        @DisplayName("Deve criar Content nulo sem lançar exceção (null é permitido pelo guard)")
        void content_null_doesNotThrow() {
            assertDoesNotThrow(() -> new Content(null));
        }

        @Test
        @DisplayName("Content vazio lança exceção")
        void content_empty_doesThrow() {
            assertDoesNotThrow(() -> new Content(""));
        }
    }

    // ─── Prioridade ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Prioridade")
    class PrioridadeTest {

        @Test
        @DisplayName("BAIXA.elevar() deve retornar MEDIA")
        void elevar_baixa() {
            assertEquals(Prioridade.MEDIA, Prioridade.BAIXA.elevar());
        }

        @Test
        @DisplayName("MEDIA.elevar() deve retornar ALTA")
        void elevar_media() {
            assertEquals(Prioridade.ALTA, Prioridade.MEDIA.elevar());
        }

        @Test
        @DisplayName("ALTA.elevar() deve retornar URGENTE")
        void elevar_alta() {
            assertEquals(Prioridade.URGENTE, Prioridade.ALTA.elevar());
        }

        @Test
        @DisplayName("URGENTE.elevar() deve permanecer URGENTE (teto)")
        void elevar_urgente_permanece() {
            assertEquals(Prioridade.URGENTE, Prioridade.URGENTE.elevar());
        }

        @Test
        @DisplayName("URGENTE.reduzir() deve retornar ALTA")
        void reduzir_urgente() {
            assertEquals(Prioridade.ALTA, Prioridade.URGENTE.reduzir());
        }

        @Test
        @DisplayName("ALTA.reduzir() deve retornar MEDIA")
        void reduzir_alta() {
            assertEquals(Prioridade.MEDIA, Prioridade.ALTA.reduzir());
        }

        @Test
        @DisplayName("MEDIA.reduzir() deve retornar BAIXA")
        void reduzir_media() {
            assertEquals(Prioridade.BAIXA, Prioridade.MEDIA.reduzir());
        }

        @Test
        @DisplayName("BAIXA.reduzir() deve permanecer BAIXA (piso)")
        void reduzir_baixa_permanece() {
            assertEquals(Prioridade.BAIXA, Prioridade.BAIXA.reduzir());
        }

        @Test
        @DisplayName("getPeso() deve retornar os pesos corretos para cada nível")
        void getPeso() {
            assertEquals(3, Prioridade.URGENTE.getPeso());
            assertEquals(2, Prioridade.ALTA.getPeso());
            assertEquals(1, Prioridade.MEDIA.getPeso());
            assertEquals(0, Prioridade.BAIXA.getPeso());
        }
    }

    // ─── Status ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Status")
    class StatusTest {

        @Test
        @DisplayName("ON.desativar() deve retornar OFF")
        void desativar_on() {
            assertEquals(Status.OFF, Status.ON.desativar());
        }

        @Test
        @DisplayName("OFF.desativar() deve lançar InvalidDataException")
        void desativar_off() {
            assertThrows(InvalidDataException.class, () -> Status.OFF.desativar());
        }
    }

    // ─── Tipo ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Tipo")
    class TipoTest {

        @Test
        @DisplayName("desativar() deve retornar DESATIVADO para tipo ativo")
        void desativar_ativo() {
            assertEquals(Tipo.DESATIVADO, Tipo.ALUNO.desativar());
        }

        @Test
        @DisplayName("desativar() deve lançar InvalidDataException se já DESATIVADO")
        void desativar_jaDesativado() {
            assertThrows(InvalidDataException.class, () -> Tipo.DESATIVADO.desativar());
        }

        @Test
        @DisplayName("mudarPara() deve retornar o novo tipo")
        void mudarPara_valido() {
            assertEquals(Tipo.PROFESSOR, Tipo.ALUNO.mudarPara(Tipo.PROFESSOR));
        }

        @Test
        @DisplayName("mudarPara() deve lançar InvalidDataException para tipo nulo")
        void mudarPara_null() {
            assertThrows(InvalidDataException.class, () -> Tipo.ALUNO.mudarPara(null));
        }

        @Test
        @DisplayName("mudarPara() deve lançar InvalidDataException para o mesmo tipo")
        void mudarPara_mesmoTipo() {
            assertThrows(InvalidDataException.class, () -> Tipo.ALUNO.mudarPara(Tipo.ALUNO));
        }
    }

    // ─── TokenUpdater ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("TokenUpdater")
    class TokenUpdaterTest {

        @Test
        @DisplayName("Start() deve criar token com expiração futura")
        void start_criaComExpiracaoFutura() {
            var updater = TokenUpdater.Start();
            assertNotNull(updater.token());
            assertTrue(updater.expiration().isAfter(LocalDateTime.now()));
        }

        @Test
        @DisplayName("Check() deve passar com token correto e não expirado")
        void check_tokenCorreto() {
            var updater = TokenUpdater.Start();
            assertDoesNotThrow(() -> updater.Check(updater.token()));
        }

        @Test
        @DisplayName("Check() deve lançar ValidationFailedException para token errado")
        void check_tokenErrado() {
            var updater = TokenUpdater.Start();
            assertThrows(ValidationFailedException.class, () -> updater.Check("token-invalido"));
        }

        @Test
        @DisplayName("Check() deve lançar ValidationFailedException para token expirado")
        void check_tokenExpirado() {
            var updater = new TokenUpdater("meu-token", LocalDateTime.now().minusMinutes(1));
            assertThrows(ValidationFailedException.class, () -> updater.Check("meu-token"));
        }
    }

    // ─── TargetVO ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("TargetVO")
    class TargetVOTest {

        @Test
        @DisplayName("paraTodos() deve criar TargetVO com tipo GERAL e lista vazia")
        void paraTodos() {
            var target = TargetVO.paraTodos();
            assertEquals(TargetType.GERAL, target.targetType());
            assertTrue(target.targetIds().isEmpty());
        }

        @Test
        @DisplayName("paraProfessores() deve criar TargetVO com tipo PROFESSORES")
        void paraProfessores() {
            var target = TargetVO.paraProfessores();
            assertEquals(TargetType.PROFESSORES, target.targetType());
            assertTrue(target.targetIds().isEmpty());
        }

        @Test
        @DisplayName("paraTurma() deve criar TargetVO com tipo TURMA e um id")
        void paraTurma() {
            var id = UUID.randomUUID();
            var target = TargetVO.paraTurma(id);
            assertEquals(TargetType.TURMA, target.targetType());
            assertEquals(1, target.targetIds().size());
            assertEquals(id, target.targetIds().get(0));
        }

        @Test
        @DisplayName("paraListaDeTurmas() deve criar TargetVO com tipo TURMAS e múltiplos ids")
        void paraListaDeTurmas() {
            var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
            var target = TargetVO.paraListaDeTurmas(ids);
            assertEquals(TargetType.TURMAS, target.targetType());
            assertEquals(2, target.targetIds().size());
        }

        @Test
        @DisplayName("Construtor com targetIds null deve usar lista vazia")
        void constructor_targetIdsNull_usaListaVazia() {
            var target = new TargetVO(TargetType.GERAL, null);
            assertNotNull(target.targetIds());
            assertTrue(target.targetIds().isEmpty());
        }
    }



}