package com.etec.zl.conecta.Domain.Entities.Turmas;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Turma")
class TurmaTest {

    // ─── Criação ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Criação via construtor simples")
    class Criacao {

        @Test
        @DisplayName("deve iniciar com status ON")
        void statusOn() {
            var turma = new Turma(Cursos.ADMINISTRACAO, 3);
            assertEquals(Status.ON, turma.getStatus());
        }

        @Test
        @DisplayName("deve gerar id automaticamente não nulo")
        void idGerado() {
            var turma = new Turma(Cursos.ADMINISTRACAO, 3);
            assertNotNull(turma.getId());
        }

        @Test
        @DisplayName("deve iniciar no módulo 1")
        void atualIniciaNaUm() {
            var turma = new Turma(Cursos.ADMINISTRACAO, 3);
            assertEquals(1, turma.getAtual());
        }

        @Test
        @DisplayName("deve preservar o número de módulos informado")
        void modulosPreservados() {
            var turma = new Turma(Cursos.ADMINISTRACAO, 5);
            assertEquals(5, turma.getModulos());
        }
    }

    // ─── passarModulo() ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("passarModulo()")
    class PassarModulo {

        @Test
        @DisplayName("deve incrementar atual de 1 para 2 quando há módulos restantes")
        void incrementa() {
            var turma = new Turma(null, Cursos.ADMINISTRACAO, 3, 1, Status.ON);
            turma.passarModulo();
            assertEquals(2, turma.getAtual());
            assertEquals(Status.ON, turma.getStatus());
        }

        @Test
        @DisplayName("deve desativar quando atual == modulos")
        void desativa_atualIgualModulos() {
            var turma = new Turma(null, Cursos.ADMINISTRACAO, 3, 3, Status.ON);
            turma.passarModulo();
            assertEquals(Status.OFF, turma.getStatus());
        }

        @Test
        @DisplayName("deve desativar quando atual > modulos")
        void desativa_atualMaiorQueModulos() {
            var turma = new Turma(null, Cursos.ADMINISTRACAO, 3, 5, Status.ON);
            turma.passarModulo();
            assertEquals(Status.OFF, turma.getStatus());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException quando já está OFF")
        void jaDesativada_lancaExcecao() {
            var turma = new Turma(null, Cursos.ADMINISTRACAO, 3, 3, Status.OFF);
            var ex = assertThrows(InvalidDataException.class, turma::passarModulo);
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("sequência completa: deve incrementar até o limite e então desativar")
        void sequenciaCompleta() {
            var turma = new Turma(null, Cursos.LOGISTICA, 3, 1, Status.ON);

            turma.passarModulo();
            assertEquals(2, turma.getAtual());
            assertEquals(Status.ON, turma.getStatus());

            turma.passarModulo();
            assertEquals(3, turma.getAtual());
            assertEquals(Status.ON, turma.getStatus());

            turma.passarModulo(); // atual == modulos → OFF
            assertEquals(Status.OFF, turma.getStatus());
        }

        @Test
        @DisplayName("não deve incrementar atual quando desativa")
        void naoIncrementaAoDesativar() {
            var turma = new Turma(null, Cursos.ADMINISTRACAO, 3, 3, Status.ON);
            turma.passarModulo();
            assertEquals(3, turma.getAtual()); // permanece 3, não vira 4
        }
    }
}