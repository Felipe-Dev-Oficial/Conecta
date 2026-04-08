package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Objects")
class ValueObjectsTests {

    // ─── Email ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Email")
    class EmailTest {

        @Test
        @DisplayName("deve preservar o email informado")
        void preservaValor() {
            assertEquals("joao@etec.com", new Email("joao@etec.com").email());
        }

        @Test
        @DisplayName("deve aceitar subdomínio")
        void comSubdominio() {
            assertEquals("user@mail.etec.sp.gov.br", new Email("user@mail.etec.sp.gov.br").email());
        }

        @Test
        @DisplayName("deve aceitar ponto e hífen na parte local")
        void comPontoEHifen() {
            assertDoesNotThrow(() -> new Email("joao.silva-123@empresa.com"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para email sem @")
        void semArroba() {
            assertThrows(InvalidDataException.class, () -> new Email("quackquack.com"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para email sem domínio")
        void semDominio() {
            assertThrows(InvalidDataException.class, () -> new Email("user@"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para email nulo")
        void nulo() {
            assertThrows(InvalidDataException.class, () -> new Email(null));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para email vazio")
        void vazio() {
            assertThrows(InvalidDataException.class, () -> new Email(""));
        }
    }

    // ─── Password ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Password")
    class PasswordTest {

        @Test
        @DisplayName("deve preservar a senha plaintext informada")
        void preservaValor() {
            assertEquals("Etec@1234", new Password("Etec@1234").password());
        }

        @Test
        @DisplayName("deve aceitar hash BCrypt $2a$")
        void bcrypt2a() {
            assertDoesNotThrow(() -> new Password("$2a$10$7EqJtq98hPqEX7fNZaFWoOa6sTXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
        }

        @Test
        @DisplayName("deve aceitar hash BCrypt $2b$")
        void bcrypt2b() {
            assertDoesNotThrow(() -> new Password("$2b$12$7EqJtq98hPqEX7fNZaFWoOa6sTXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para senha sem letra maiúscula")
        void semMaiuscula() {
            assertThrows(InvalidDataException.class, () -> new Password("etec@1234"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para senha sem caractere especial")
        void semEspecial() {
            assertThrows(InvalidDataException.class, () -> new Password("Etec1234"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para senha sem número")
        void semNumero() {
            assertThrows(InvalidDataException.class, () -> new Password("Etec@abcd"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para senha com menos de 8 caracteres")
        void muitoCurta() {
            assertThrows(InvalidDataException.class, () -> new Password("Et@1"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para senha nula")
        void nula() {
            assertThrows(InvalidDataException.class, () -> new Password(null));
        }
    }

    // ─── Name ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Name")
    class NameTest {

        @Test
        @DisplayName("deve preservar o nome informado")
        void preservaValor() {
            assertEquals("João Silva", new Name("João Silva").name());
        }

        @Test
        @DisplayName("deve aceitar nome com acentos e caracteres Unicode")
        void comAcentos() {
            assertEquals("Mônica Ângela", new Name("Mônica Ângela").name());
        }

        @Test
        @DisplayName("deve aceitar nome com uma única palavra de mais de 1 caractere")
        void palavraUnica() {
            assertDoesNotThrow(() -> new Name("Maria"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para name nulo")
        void nulo() {
            assertThrows(InvalidDataException.class, () -> new Name(null));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para name vazio")
        void vazio() {
            assertThrows(InvalidDataException.class, () -> new Name(""));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para name com apenas 1 caractere")
        void umCaractere() {
            assertThrows(InvalidDataException.class, () -> new Name("A"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para name com números")
        void comNumeros() {
            assertThrows(InvalidDataException.class, () -> new Name("João123"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para name começando com espaço")
        void comecaComEspaco() {
            assertThrows(InvalidDataException.class, () -> new Name(" João"));
        }
    }

    // ─── PhoneNumber ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PhoneNumber")
    class PhoneNumberTest {

        @Test
        @DisplayName("deve preservar o número informado")
        void preservaValor() {
            assertEquals("11987654321", new PhoneNumber("11987654321").number());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para número nulo")
        void nulo() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber(null));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para número sem nono dígito 9")
        void semNonoDígito() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("11887654321"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para DDD começando com 0")
        void dddComZero() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("01987654321"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para número com menos dígitos")
        void muitoCurto() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("1198765432"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para número com mais dígitos")
        void muitoLongo() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("119876543210"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para número com letras")
        void comLetras() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber("1198765432A"));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para número vazio")
        void vazio() {
            assertThrows(InvalidDataException.class, () -> new PhoneNumber(""));
        }
    }

    // ─── Content ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Content")
    class ContentTest {

        @Test
        @DisplayName("deve preservar o texto informado")
        void preservaValor() {
            assertEquals("Algum conteúdo", new Content("Algum conteúdo").content());
        }

        @Test
        @DisplayName("deve aceitar texto longo sem lançar exceção")
        void textoLongo() {
            String longo = "a".repeat(5000);
            assertEquals(longo, new Content(longo).content());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para Content nulo")
        void nulo() {
            assertThrows(InvalidDataException.class, () -> new Content(null));
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para Content vazio")
        void vazio() {
            assertThrows(InvalidDataException.class, () -> new Content(""));
        }
    }

    // ─── PageRequest ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PageRequest")
    class PageRequestTest {

        @Test
        @DisplayName("deve preservar page e size informados")
        void preservaValores() {
            var pr = new PageRequest(2, 15);
            assertEquals(2, pr.page());
            assertEquals(15, pr.size());
        }

        @Test
        @DisplayName("deve aceitar page=0 e size=10")
        void paginaZero() {
            assertDoesNotThrow(() -> new PageRequest(0, 10));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para page negativo")
        void pageNegativo() {
            assertThrows(IllegalArgumentException.class, () -> new PageRequest(-1, 10));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para size zero")
        void sizeZero() {
            assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 0));
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException para size negativo")
        void sizeNegativo() {
            assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, -5));
        }
    }

    // ─── PageResult ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("PageResult")
    class PageResultTest {

        @Test
        @DisplayName("deve preservar todos os campos informados")
        void preservaCampos() {
            var content = List.of("a", "b");
            var result = new PageResult<>(content, 0, 2, 10L, 5);

            assertEquals(content, result.content());
            assertEquals(0, result.page());
            assertEquals(2, result.size());
            assertEquals(10L, result.totalElements());
            assertEquals(5, result.totalPages());
        }

        @Test
        @DisplayName("map() deve transformar cada item preservando paginação intacta")
        void map_transformaConteudo() {
            var result = new PageResult<>(List.of("1", "2"), 0, 2, 2L, 1);
            var mapped = result.map(Integer::parseInt);

            assertEquals(List.of(1, 2), mapped.content());
            assertEquals(0, mapped.page());
            assertEquals(2, mapped.size());
            assertEquals(2L, mapped.totalElements());
            assertEquals(1, mapped.totalPages());
        }
    }

    // ─── SliceResult ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("SliceResult")
    class SliceResultTest {

        @Test
        @DisplayName("deve indicar hasNext=true quando há próxima página")
        void hasNext_verdadeiro() {
            var result = new SliceResult<>(List.of("x", "y"), 0, 2, true);
            assertTrue(result.hasNext());
        }

        @Test
        @DisplayName("deve indicar hasNext=false na última página")
        void hasNext_falso() {
            var result = new SliceResult<>(List.of("z"), 3, 2, false);
            assertFalse(result.hasNext());
        }

        @Test
        @DisplayName("map() deve transformar conteúdo preservando slice info intacta")
        void map_transformaConteudo() {
            var result = new SliceResult<>(List.of("10", "20"), 1, 2, true);
            var mapped = result.map(Integer::parseInt);

            assertEquals(List.of(10, 20), mapped.content());
            assertEquals(1, mapped.page());
            assertEquals(2, mapped.size());
            assertTrue(mapped.hasNext());
        }
    }

    // ─── Prioridade ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Prioridade")
    class PrioridadeTest {

        @Test
        @DisplayName("getPeso() deve retornar valores corretos para cada nível")
        void getPeso() {
            assertEquals(0, Prioridade.BAIXA.getPeso());
            assertEquals(1, Prioridade.MEDIA.getPeso());
            assertEquals(2, Prioridade.ALTA.getPeso());
            assertEquals(3, Prioridade.URGENTE.getPeso());
        }

        @Test
        @DisplayName("fromPeso() deve retornar o Enum correto para pesos de 0 a 3")
        void fromPeso_valido() {
            assertEquals(Prioridade.BAIXA,   Prioridade.fromPeso(0));
            assertEquals(Prioridade.MEDIA,   Prioridade.fromPeso(1));
            assertEquals(Prioridade.ALTA,    Prioridade.fromPeso(2));
            assertEquals(Prioridade.URGENTE, Prioridade.fromPeso(3));
        }

        @Test
        @DisplayName("fromPeso() deve lançar ProcessingErrorException para pesos inválidos")
        void fromPeso_invalido() {
            assertThrows(ProcessingErrorException.class, () -> Prioridade.fromPeso(-1));
            assertThrows(ProcessingErrorException.class, () -> Prioridade.fromPeso(4));
            assertThrows(ProcessingErrorException.class, () -> Prioridade.fromPeso(99));
        }

        @Test
        @DisplayName("elevar() deve subir BAIXA → MEDIA → ALTA → URGENTE")
        void elevar_cadeia() {
            assertEquals(Prioridade.MEDIA,   Prioridade.BAIXA.elevar());
            assertEquals(Prioridade.ALTA,    Prioridade.MEDIA.elevar());
            assertEquals(Prioridade.URGENTE, Prioridade.ALTA.elevar());
        }

        @Test
        @DisplayName("elevar() em URGENTE deve permanecer URGENTE (teto)")
        void elevar_teto() {
            assertEquals(Prioridade.URGENTE, Prioridade.URGENTE.elevar());
        }

        @Test
        @DisplayName("reduzir() deve descer URGENTE → ALTA → MEDIA → BAIXA")
        void reduzir_cadeia() {
            assertEquals(Prioridade.ALTA,  Prioridade.URGENTE.reduzir());
            assertEquals(Prioridade.MEDIA, Prioridade.ALTA.reduzir());
            assertEquals(Prioridade.BAIXA, Prioridade.MEDIA.reduzir());
        }

        @Test
        @DisplayName("reduzir() em BAIXA deve permanecer BAIXA (piso)")
        void reduzir_piso() {
            assertEquals(Prioridade.BAIXA, Prioridade.BAIXA.reduzir());
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
            var ex = assertThrows(InvalidDataException.class, () -> Status.OFF.desativar());
            assertNotNull(ex.getMessage());
        }
    }

    // ─── Tipo ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Tipo")
    class TipoTest {

        @Test
        @DisplayName("desativar() deve retornar DESATIVADO para cada tipo ativo")
        void desativar_tiposAtivos() {
            assertEquals(Tipo.DESATIVADO, Tipo.ALUNO.desativar());
            assertEquals(Tipo.DESATIVADO, Tipo.PROFESSOR.desativar());
            assertEquals(Tipo.DESATIVADO, Tipo.SECRETARIA.desativar());
        }

        @Test
        @DisplayName("desativar() deve lançar InvalidDataException se já DESATIVADO")
        void desativar_jaDesativado() {
            var ex = assertThrows(InvalidDataException.class, () -> Tipo.DESATIVADO.desativar());
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("mudarPara() deve retornar exatamente o novo tipo")
        void mudarPara_valido() {
            assertEquals(Tipo.PROFESSOR, Tipo.ALUNO.mudarPara(Tipo.PROFESSOR));
            assertEquals(Tipo.SECRETARIA, Tipo.PROFESSOR.mudarPara(Tipo.SECRETARIA));
        }

        @Test
        @DisplayName("mudarPara() deve permitir reativar de DESATIVADO para qualquer tipo")
        void mudarPara_reativa() {
            assertEquals(Tipo.ALUNO,      Tipo.DESATIVADO.mudarPara(Tipo.ALUNO));
            assertEquals(Tipo.PROFESSOR,  Tipo.DESATIVADO.mudarPara(Tipo.PROFESSOR));
        }

        @Test
        @DisplayName("mudarPara() deve lançar InvalidDataException para tipo nulo")
        void mudarPara_nulo() {
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
        @DisplayName("Start() deve criar token não nulo e não vazio")
        void start_tokenNaoVazio() {
            var updater = TokenUpdater.Start();
            assertNotNull(updater.token());
            assertFalse(updater.token().isBlank());
        }

        @Test
        @DisplayName("Start() deve criar expiração no futuro")
        void start_expiracaoFutura() {
            var updater = TokenUpdater.Start();
            assertTrue(updater.expiration().isAfter(LocalDateTime.now()));
        }

        @Test
        @DisplayName("dois Start() consecutivos devem gerar tokens distintos")
        void start_tokensUnicos() {
            assertNotEquals(TokenUpdater.Start().token(), TokenUpdater.Start().token());
        }

        @Test
        @DisplayName("Check() deve passar sem exceção com token correto e não expirado")
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
        @DisplayName("paraProfessores() deve criar TargetVO com tipo PROFESSORES e lista vazia")
        void paraProfessores() {
            var target = TargetVO.paraProfessores();
            assertEquals(TargetType.PROFESSORES, target.targetType());
            assertTrue(target.targetIds().isEmpty());
        }

        @Test
        @DisplayName("paraTurma() deve criar TargetVO com tipo TURMA e exatamente o id informado")
        void paraTurma() {
            var target = TargetVO.paraTurma("turma-abc");
            assertEquals(TargetType.TURMA, target.targetType());
            assertEquals(1, target.targetIds().size());
            assertEquals("turma-abc", target.targetIds().get(0));
        }

        @Test
        @DisplayName("paraListaDeTurmas() deve criar TargetVO com tipo TURMAS e todos os ids")
        void paraListaDeTurmas() {
            var ids = List.of("a", "b", "c");
            var target = TargetVO.paraListaDeTurmas(ids);
            assertEquals(TargetType.TURMAS, target.targetType());
            assertEquals(3, target.targetIds().size());
            assertEquals(ids, target.targetIds());
        }

        @Test
        @DisplayName("construtor com targetIds null deve resultar em lista vazia")
        void targetIdsNulo_usaListaVazia() {
            var target = new TargetVO(TargetType.GERAL, null);
            assertNotNull(target.targetIds());
            assertTrue(target.targetIds().isEmpty());
        }

        @Test
        @DisplayName("construtor com lista válida deve preservá-la integralmente")
        void targetIdsValidos_preservados() {
            var ids = List.of("x", "y");
            var target = new TargetVO(TargetType.ALUNOS, ids);
            assertEquals(ids, target.targetIds());
        }
    }
}