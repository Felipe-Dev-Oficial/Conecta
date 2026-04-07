package com.etec.zl.conecta.Domain.Entities.Users;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User")
class UserTest {

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

    private User userComUpdaters() {
        return new User(
                "user-2",
                new Name("Maria Souza"),
                new Email("maria@etec.com"),
                new PhoneNumber("11987654322"),
                new Password("Etec@1234"),
                Tipo.PROFESSOR,
                TokenUpdater.Start(),
                TokenUpdater.Start(),
                new ArrayList<>()
        );
    }

    // ─── alteraNome() ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("alteraNome()")
    class AlteraNome {

        @Test
        @DisplayName("deve atualizar o nome para o novo valor")
        void valido() {
            var user = userPadrao();
            user.alteraNome(new Name("Carlos Lima"));
            assertEquals("Carlos Lima", user.getNome().name());
        }

        @Test
        @DisplayName("deve preservar o nome anterior até a chamada")
        void preservaAnterior() {
            var user = userPadrao();
            assertEquals("João Silva", user.getNome().name());
        }
    }

    // ─── alteraTipo() ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("alteraTipo()")
    class AlteraTipo {

        @Test
        @DisplayName("deve atualizar o tipo para o novo valor")
        void sucesso() {
            var user = userPadrao();
            user.alteraTipo(Tipo.PROFESSOR);
            assertEquals(Tipo.PROFESSOR, user.getTipo());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para o mesmo tipo")
        void mesmoTipo() {
            var user = userPadrao();
            var ex = assertThrows(InvalidDataException.class, () -> user.alteraTipo(Tipo.ALUNO));
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException para tipo nulo")
        void tipoNulo() {
            var user = userPadrao();
            assertThrows(InvalidDataException.class, () -> user.alteraTipo(null));
        }

        @Test
        @DisplayName("não deve alterar o tipo quando lança exceção")
        void naoAlteraEmCasoDeExcecao() {
            var user = userPadrao();
            assertThrows(InvalidDataException.class, () -> user.alteraTipo(Tipo.ALUNO));
            assertEquals(Tipo.ALUNO, user.getTipo());
        }
    }

    // ─── desativa() ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("desativa()")
    class Desativa {

        @Test
        @DisplayName("deve mudar tipo para DESATIVADO")
        void sucesso() {
            var user = userPadrao();
            user.desativa();
            assertEquals(Tipo.DESATIVADO, user.getTipo());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException se já DESATIVADO")
        void jaDesativado() {
            var user = new User(
                    "user-x", new Name("Test User"), new Email("test@etec.com"),
                    new PhoneNumber("11987654321"), new Password("Etec@1234"),
                    Tipo.DESATIVADO, new ArrayList<>()
            );
            var ex = assertThrows(InvalidDataException.class, user::desativa);
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("não deve alterar o tipo quando já DESATIVADO e lança exceção")
        void naoAlteraEmCasoDeExcecao() {
            var user = new User(
                    "user-x", new Name("Test User"), new Email("test@etec.com"),
                    new PhoneNumber("11987654321"), new Password("Etec@1234"),
                    Tipo.DESATIVADO, new ArrayList<>()
            );
            assertThrows(InvalidDataException.class, user::desativa);
            assertEquals(Tipo.DESATIVADO, user.getTipo());
        }
    }

    // ─── addTurmas() ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("addTurmas()")
    class AddTurmas {

        @Test
        @DisplayName("deve adicionar turmas e o tamanho da lista deve refletir isso")
        void adiciona() {
            var user = userPadrao();
            user.addTurmas(List.of("turma-1", "turma-2"));
            assertEquals(2, user.getTurmasIds().size());
            assertTrue(user.getTurmasIds().contains("turma-1"));
            assertTrue(user.getTurmasIds().contains("turma-2"));
        }

        @Test
        @DisplayName("não deve adicionar turma duplicada — lista permanece com tamanho 1")
        void duplicadaNaoAdiciona() {
            var user = userPadrao();
            user.addTurmas(List.of("turma-1"));
            user.addTurmas(List.of("turma-1"));
            assertEquals(1, user.getTurmasIds().size());
        }

        @Test
        @DisplayName("deve inicializar lista e adicionar quando turmasIds for null")
        void listaNulaInicializa() {
            var user = new User(
                    "user-3", new Name("Pedro Costa"), new Email("pedro@etec.com"),
                    new PhoneNumber("11987654323"), new Password("Etec@1234"),
                    Tipo.ALUNO, null
            );
            user.addTurmas(List.of("turma-99"));
            assertEquals(1, user.getTurmasIds().size());
            assertEquals("turma-99", user.getTurmasIds().get(0));
        }

        @Test
        @DisplayName("segunda chamada acumula com turmas já existentes")
        void acumulaComExistentes() {
            var user = userPadrao();
            user.addTurmas(List.of("turma-1"));
            user.addTurmas(List.of("turma-2", "turma-3"));
            assertEquals(3, user.getTurmasIds().size());
        }
    }

    // ─── checkAndChangeEmail() ────────────────────────────────────────────────

    @Nested
    @DisplayName("checkAndChangeEmail()")
    class CheckAndChangeEmail {

        @Test
        @DisplayName("deve atualizar o email quando token é válido")
        void tokenValido() {
            var user = userComUpdaters();
            var token = UUID.fromString(user.getEmailUpdater().token());
            user.checkAndChangeEmail(token, new Email("novo@etec.com"));
            assertEquals("novo@etec.com", user.getEmail().email());
        }

        @Test
        @DisplayName("deve zerar emailUpdater após troca bem-sucedida")
        void zeraUpdaterAposTroca() {
            var user = userComUpdaters();
            var token = UUID.fromString(user.getEmailUpdater().token());
            user.checkAndChangeEmail(token, new Email("novo@etec.com"));
            assertNull(user.getEmailUpdater());
        }

        @Test
        @DisplayName("deve lançar ValidationFailedException para token errado")
        void tokenErrado() {
            var user = userComUpdaters();
            var emailOriginal = user.getEmail().email();
            assertThrows(ValidationFailedException.class,
                    () -> user.checkAndChangeEmail(UUID.randomUUID(), new Email("novo@etec.com")));
            assertEquals(emailOriginal, user.getEmail().email());
        }
    }

    // ─── checkAndChangePassword() ─────────────────────────────────────────────

    @Nested
    @DisplayName("checkAndChangePassword()")
    class CheckAndChangePassword {

        @Test
        @DisplayName("deve atualizar a senha quando token é válido")
        void tokenValido() {
            var user = userComUpdaters();
            var token = UUID.fromString(user.getPasswordUpdater().token());
            user.checkAndChangePassword(token, new Password("NovaEtec@5678"));
            assertEquals("NovaEtec@5678", user.getSenha().password());
        }

        @Test
        @DisplayName("deve zerar passwordUpdater após troca bem-sucedida")
        void zeraUpdaterAposTroca() {
            var user = userComUpdaters();
            var token = UUID.fromString(user.getPasswordUpdater().token());
            user.checkAndChangePassword(token, new Password("NovaEtec@5678"));
            assertNull(user.getPasswordUpdater());
        }

        @Test
        @DisplayName("deve lançar ValidationFailedException para token errado")
        void tokenErrado() {
            var user = userComUpdaters();
            assertThrows(ValidationFailedException.class,
                    () -> user.checkAndChangePassword(UUID.randomUUID(), new Password("NovaEtec@5678")));
        }

        @Test
        @DisplayName("senha não deve ser alterada quando token for errado")
        void senhaIntactaComTokenErrado() {
            var user = userComUpdaters();
            var senhaOriginal = user.getSenha().password();
            assertThrows(ValidationFailedException.class,
                    () -> user.checkAndChangePassword(UUID.randomUUID(), new Password("NovaEtec@5678")));
            assertEquals(senhaOriginal, user.getSenha().password());
        }
    }

    // ─── sendUpdateEmailToken() / sendUpdatePasswordToken() ──────────────────

    @Nested
    @DisplayName("sendUpdateTokens()")
    class SendUpdateTokens {

        @Test
        @DisplayName("sendUpdateEmailToken() deve popular emailUpdater com token não nulo")
        void emailUpdaterPopulado() {
            var user = userPadrao();
            user.sendUpdateEmailToken();
            assertNotNull(user.getEmailUpdater());
            assertFalse(user.getEmailUpdater().token().isBlank());
        }

        @Test
        @DisplayName("sendUpdatePasswordToken() deve popular passwordUpdater com token não nulo")
        void passwordUpdaterPopulado() {
            var user = userPadrao();
            user.sendUpdatePasswordToken();
            assertNotNull(user.getPasswordUpdater());
            assertFalse(user.getPasswordUpdater().token().isBlank());
        }
    }
}