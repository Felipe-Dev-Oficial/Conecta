package com.etec.zl.conecta.Domain.Entities.Turmas;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Turma")
class TurmaTest {

    // ─── Criação ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar Turma com status ON por padrão via construtor simples")
    void criacao_statusOn() {
        var turma = new Turma(Cursos.ADMINISTRACAO, 3);
        assertEquals(Status.ON, turma.getStatus());
    }

    @Test
    @DisplayName("Deve criar Turma com id gerado automaticamente via construtor simples")
    void criacao_idGerado() {
        var turma = new Turma(Cursos.ADMINISTRACAO, 3);
        assertNotNull(turma.getId());
    }

    // ─── passarModulo() ───────────────────────────────────────────────────────

    @Test
    @DisplayName("passarModulo() deve incrementar atual quando há módulos restantes")
    void passarModulo_incrementa() {
        var turma = new Turma(UUID.randomUUID(), Cursos.ADMINISTRACAO, 3, 1, Status.ON);
        turma.passarModulo();
        assertEquals(2, turma.getAtual());
        assertEquals(Status.ON, turma.getStatus());
    }

    @Test
    @DisplayName("passarModulo() deve desativar turma quando atual == modulos")
    void passarModulo_desativa_quandoAtualIgualModulos() {
        var turma = new Turma(UUID.randomUUID(), Cursos.ADMINISTRACAO, 3, 3, Status.ON);
        turma.passarModulo();
        assertEquals(Status.OFF, turma.getStatus());
    }

    @Test
    @DisplayName("passarModulo() deve desativar turma quando atual > modulos")
    void passarModulo_desativa_quandoAtualMaiorQueModulos() {
        var turma = new Turma(UUID.randomUUID(), Cursos.ADMINISTRACAO, 3, 5, Status.ON);
        turma.passarModulo();
        assertEquals(Status.OFF, turma.getStatus());
    }

    @Test
    @DisplayName("passarModulo() em turma já OFF deve lançar InvalidDataException")
    void passarModulo_jaDesativada() {
        var turma = new Turma(UUID.randomUUID(), Cursos.ADMINISTRACAO, 3, 3, Status.OFF);
        assertThrows(InvalidDataException.class, turma::passarModulo);
    }

    @Test
    @DisplayName("passarModulo() múltiplas vezes deve incrementar corretamente até o limite")
    void passarModulo_multiplas_vezes() {
        var turma = new Turma(UUID.randomUUID(), Cursos.LOGISTICA, 3, 1, Status.ON);
        turma.passarModulo(); // 1 -> 2
        turma.passarModulo(); // 2 -> 3
        assertEquals(3, turma.getAtual());
        assertEquals(Status.ON, turma.getStatus());
        turma.passarModulo(); // 3 == 3 -> OFF
        assertEquals(Status.OFF, turma.getStatus());
    }
}