package com.etec.zl.conecta.Domain.Entities.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Statement Entity Test")
class StatementTest {

    private static final String SENDER_ID = "sender-123";

    private Statement statementOn() {
        return new Statement(
                UUID.randomUUID(), SENDER_ID,
                "Título Original", Instant.now(),
                "Conteúdo Original", null,
                Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos()
        );
    }

    @Nested
    @DisplayName("Criação e Regras de Negócio")
    class Criacao {

        @Test
        @DisplayName("deve iniciar com valores padrão (status ON, edited FALSE)")
        void valoresPadrao() {
            var s = new Statement(SENDER_ID, "Título", "Conteúdo", null, Prioridade.MEDIA, TargetVO.paraTodos());

            assertNotNull(s.getId());
            assertEquals(Status.ON, s.getStatus());
            assertFalse(s.isEdited());
            assertNotNull(s.getTimestamp());
        }

        @Test
        @DisplayName("deve permitir criar anúncio apenas com mídia (sem título/conteúdo)")
        void apenasMidia() {
            var midia = new Midia(TipoMidia.FOTO, "http://link.com");
            assertDoesNotThrow(() ->
                    new Statement(SENDER_ID, null, null, midia, Prioridade.MEDIA, TargetVO.paraTodos())
            );
        }

        @Test
        @DisplayName("deve lançar exceção se tudo (título, conteúdo e mídia) estiver vazio")
        void erroAnuncioVazio() {
            assertThrows(ProcessingErrorException.class, () ->
                    new Statement(SENDER_ID, "", "", null, Prioridade.MEDIA, TargetVO.paraTodos())
            );
        }
    }

    @Nested
    @DisplayName("Comportamento de Edição")
    class Edicao {

        @Test
        @DisplayName("deve marcar edited=true ao alterar título")
        void alteraTitulo() {
            var s = statementOn();
            s.alterarTitulo("Novo Título");
            assertEquals("Novo Título", s.getTitle());
            assertTrue(s.isEdited());
        }

        @Test
        @DisplayName("deve marcar edited=true ao alterar conteúdo")
        void alteraConteudo() {
            var s = statementOn();
            s.alterarConteudo("Novo Conteúdo");
            assertTrue(s.isEdited());
        }

        @Test
        @DisplayName("não deve marcar edited se o novo título for nulo ou vazio")
        void alteraTituloInvalido() {
            var s = statementOn();
            s.alterarTitulo("");
            assertFalse(s.isEdited());
            assertEquals("Título Original", s.getTitle());
        }
    }

    @Nested
    @DisplayName("Gestão de Prioridade")
    class Prioridades {

        @Test
        @DisplayName("deve elevar e reduzir prioridade corretamente")
        void fluxoPrioridade() {
            var s = statementOn(); // Inicia MEDIA (Peso 1)
            assertEquals(1, s.getPesoPrioridade());

            s.elevarPrioridade();
            assertEquals(Prioridade.ALTA, s.getPriority());

            s.reduzirPrioridade();
            assertEquals(Prioridade.MEDIA, s.getPriority());
        }
    }

    @Nested
    @DisplayName("Ciclo de Vida (Status)")
    class CicloVida {

        @Test
        @DisplayName("deve desativar anúncio (apagar)")
        void apagar() {
            var s = statementOn();
            s.apagarAnuncio();
            assertEquals(Status.OFF, s.getStatus());
        }

        @Test
        @DisplayName("deve lançar erro ao apagar anúncio já desativado")
        void apagarJaDesativado() {
            var s = statementOn();
            s.apagarAnuncio();

            assertThrows(InvalidDataException.class, s::apagarAnuncio);
        }
    }
}