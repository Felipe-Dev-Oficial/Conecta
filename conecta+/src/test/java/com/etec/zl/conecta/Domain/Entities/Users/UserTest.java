package com.etec.zl.conecta.Domain.Entities.Users;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
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
        var passwordUpdater = TokenUpdater.Start();
        var emailUpdater = TokenUpdater.Start();
        return new User(
                "user-2",
                new Name("Maria Souza"),
                new Email("maria@etec.com"),
                new PhoneNumber("11987654322"),
                new Password("Etec@1234"),
                Tipo.PROFESSOR,
                passwordUpdater,
                emailUpdater,
                new ArrayList<>()
        );
    }

    // ─── alteraNome() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("alteraNome() deve atualizar o nome")
    void alteraNome_valido() {
        var user = userPadrao();
        user.alteraNome(new Name("Carlos Lima"));
        assertEquals("Carlos Lima", user.getNome().name());
    }

    // ─── alteraTipo() ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("alteraTipo() deve atualizar o tipo com sucesso")
    void alteraTipo_sucesso() {
        var user = userPadrao();
        user.alteraTipo(Tipo.PROFESSOR);
        assertEquals(Tipo.PROFESSOR, user.getTipo());
    }

    @Test
    @DisplayName("alteraTipo() deve lançar InvalidDataException para o mesmo tipo")
    void alteraTipo_mesmoTipo_throwsInvalidData() {
        var user = userPadrao();
        assertThrows(InvalidDataException.class, () -> user.alteraTipo(Tipo.ALUNO));
    }

    @Test
    @DisplayName("alteraTipo() deve lançar InvalidDataException para tipo nulo")
    void alteraTipo_null_throwsInvalidData() {
        var user = userPadrao();
        assertThrows(InvalidDataException.class, () -> user.alteraTipo(null));
    }

    // ─── desativa() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("desativa() deve mudar tipo para DESATIVADO")
    void desativa_sucesso() {
        var user = userPadrao();
        user.desativa();
        assertEquals(Tipo.DESATIVADO, user.getTipo());
    }

    @Test
    @DisplayName("desativa() deve lançar InvalidDataException se tipo já for DESATIVADO")
    void desativa_jaDesativado_throwsInvalidData() {
        var user = new User(
                "user-x", new Name("Test User"), new Email("test@etec.com"),
                new PhoneNumber("11987654321"), new Password("Etec@1234"),
                Tipo.DESATIVADO, new ArrayList<>()
        );
        assertThrows(InvalidDataException.class, user::desativa);
    }

    // ─── addTurmas() ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("addTurmas() deve adicionar turmas à lista")
    void addTurmas_valido() {
        var user = userPadrao();
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        user.addTurmas(List.of(id1, id2));
        assertEquals(2, user.getTurmasIds().size());
        assertTrue(user.getTurmasIds().contains(id1));
        assertTrue(user.getTurmasIds().contains(id2));
    }

    @Test
    @DisplayName("addTurmas() não deve adicionar turma duplicada")
    void addTurmas_duplicada_naoAdiciona() {
        var user = userPadrao();
        var id = UUID.randomUUID();
        user.addTurmas(List.of(id));
        user.addTurmas(List.of(id));
        assertEquals(1, user.getTurmasIds().size());
    }

    @Test
    @DisplayName("addTurmas() deve inicializar lista quando turmasIds for null")
    void addTurmas_listaNula_inicializa() {
        var user = new User(
                "user-3", new Name("Pedro Costa"), new Email("pedro@etec.com"),
                new PhoneNumber("11987654323"), new Password("Etec@1234"),
                Tipo.ALUNO, null
        );
        var id = UUID.randomUUID();
        assertDoesNotThrow(() -> user.addTurmas(List.of(id)));
        assertEquals(1, user.getTurmasIds().size());
    }

    // ─── checkAndChangeEmail() ────────────────────────────────────────────────

    @Test
    @DisplayName("checkAndChangeEmail() deve atualizar email com token válido")
    void checkAndChangeEmail_valido() {
        var user = userComUpdaters();
        var token = UUID.fromString(user.getEmailUpdater().token());
        var novoEmail = new Email("novo@etec.com");
        user.checkAndChangeEmail(token, novoEmail);
        assertEquals("novo@etec.com", user.getEmail().email());
    }

    @Test
    @DisplayName("checkAndChangeEmail() deve lançar ValidationFailedException para token errado")
    void checkAndChangeEmail_tokenErrado() {
        var user = userComUpdaters();
        assertThrows(ValidationFailedException.class,
                () -> user.checkAndChangeEmail(UUID.randomUUID(), new Email("novo@etec.com")));
    }

    // ─── checkAndChangePassword() ─────────────────────────────────────────────

    @Test
    @DisplayName("checkAndChangePassword() deve atualizar senha com token válido")
    void checkAndChangePassword_valido() {
        var user = userComUpdaters();
        var token = UUID.fromString(user.getPasswordUpdater().token());
        user.checkAndChangePassword(token, new Password("NovaEtec@5678"));
        assertEquals("NovaEtec@5678", user.getSenha().password());
    }

    @Test
    @DisplayName("checkAndChangePassword() deve lançar ValidationFailedException para token errado")
    void checkAndChangePassword_tokenErrado() {
        var user = userComUpdaters();
        assertThrows(ValidationFailedException.class,
                () -> user.checkAndChangePassword(UUID.randomUUID(), new Password("NovaEtec@5678")));
    }
}