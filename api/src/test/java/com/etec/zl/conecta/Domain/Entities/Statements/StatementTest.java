package com.etec.zl.conecta.Domain.Entities.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Statement")
class StatementTest {

    private Statement statementOn() {
        return new Statement(
                UUID.randomUUID(), "sender-1",
                new Content("Título"), Instant.now(),
                new Content("Conteúdo"), null,
                Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos()
        );
    }

    private Statement statementOff() {
        return new Statement(
                UUID.randomUUID(), "sender-1",
                new Content("Título"), Instant.now(),
                new Content("Conteúdo"), null,
                Prioridade.MEDIA, false, Status.OFF, TargetVO.paraTodos()
        );
    }

    // ─── Criação ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Criação via construtor simples")
    class Criacao {

        @Test
        @DisplayName("deve iniciar com edited=false")
        void editedFalse() {
            var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
            assertFalse(s.isEdited());
        }

        @Test
        @DisplayName("deve gerar id não nulo automaticamente")
        void idGerado() {
            var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
            assertNotNull(s.getId());
        }

        @Test
        @DisplayName("deve preencher timestamp automaticamente")
        void timestamp() {
            var antes = Instant.now();
            var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
            assertFalse(s.getTimestamp().isBefore(antes));
        }

        @Test
        @DisplayName("deve iniciar com status ON")
        void statusOn() {
            var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
            assertEquals(Status.ON, s.getStatus());
        }
    }

    // ─── alterarTitulo() ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarTitulo()")
    class AlterarTitulo {

        @Test
        @DisplayName("deve atualizar título para o novo valor e marcar edited=true")
        void valido() {
            var s = statementOn();
            s.alterarTitulo(new Content("Novo título"));
            assertEquals("Novo título", s.getTitle().content());
            assertTrue(s.isEdited());
        }

        @Test
        @DisplayName("null não deve alterar título nem marcar edited")
        void nulo_naoAtualiza() {
            var s = statementOn();
            Content original = s.getTitle();
            s.alterarTitulo(null);
            assertEquals(original, s.getTitle());
            assertFalse(s.isEdited());
        }
    }

    // ─── alterarConteudo() ────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarConteudo()")
    class AlterarConteudo {

        @Test
        @DisplayName("deve atualizar conteúdo para o novo valor e marcar edited=true")
        void valido() {
            var s = statementOn();
            s.alterarConteudo(new Content("Novo conteúdo"));
            assertEquals("Novo conteúdo", s.getContent().content());
            assertTrue(s.isEdited());
        }

        @Test
        @DisplayName("null não deve alterar conteúdo nem marcar edited")
        void nulo_naoAtualiza() {
            var s = statementOn();
            Content original = s.getContent();
            s.alterarConteudo(null);
            assertEquals(original, s.getContent());
            assertFalse(s.isEdited());
        }
    }

    // ─── alterarMidia() ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarMidia()")
    class AlterarMidia {

        @Test
        @DisplayName("deve atualizar mídia para o novo valor e marcar edited=true")
        void valido() {
            var s = statementOn();
            var midia = new Midia(TipoMidia.FOTO, "https://img.com/foto.jpg");
            s.alterarMidia(midia);
            assertEquals(midia, s.getMidia());
            assertTrue(s.isEdited());
        }

        @Test
        @DisplayName("link vazio não deve atualizar mídia nem marcar edited")
        void linkVazio_naoAtualiza() {
            var s = statementOn();
            s.alterarMidia(new Midia(TipoMidia.FOTO, ""));
            assertNull(s.getMidia());
            assertFalse(s.isEdited());
        }

        @Test
        @DisplayName("null não deve atualizar mídia nem marcar edited")
        void nulo_naoAtualiza() {
            var s = statementOn();
            s.alterarMidia(null);
            assertNull(s.getMidia());
            assertFalse(s.isEdited());
        }
    }

    // ─── alterarPrioridade() ──────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarPrioridade()")
    class AlterarPrioridade {

        @Test
        @DisplayName("deve atualizar para o novo valor de prioridade")
        void valido() {
            var s = statementOn();
            s.alterarPrioridade(Prioridade.URGENTE);
            assertEquals(Prioridade.URGENTE, s.getPriority());
        }

        @Test
        @DisplayName("null não deve alterar a prioridade atual")
        void nulo_naoAtualiza() {
            var s = statementOn();
            s.alterarPrioridade(null);
            assertEquals(Prioridade.MEDIA, s.getPriority());
        }
    }

    // ─── elevarPrioridade() / reduzirPrioridade() ─────────────────────────────

    @Nested
    @DisplayName("elevar/reduzir prioridade")
    class ElevarReduzirPrioridade {

        @Test
        @DisplayName("elevarPrioridade() deve subir MEDIA para ALTA")
        void elevar_media_paraAlta() {
            var s = statementOn(); // MEDIA
            s.elevarPrioridade();
            assertEquals(Prioridade.ALTA, s.getPriority());
        }

        @Test
        @DisplayName("elevarPrioridade() deve subir ALTA para URGENTE")
        void elevar_alta_paraUrgente() {
            var s = statementOn();
            s.alterarPrioridade(Prioridade.ALTA);
            s.elevarPrioridade();
            assertEquals(Prioridade.URGENTE, s.getPriority());
        }

        @Test
        @DisplayName("reduzirPrioridade() deve descer MEDIA para BAIXA")
        void reduzir_media_paraBaixa() {
            var s = statementOn(); // MEDIA
            s.reduzirPrioridade();
            assertEquals(Prioridade.BAIXA, s.getPriority());
        }

        @Test
        @DisplayName("reduzirPrioridade() deve descer ALTA para MEDIA")
        void reduzir_alta_paraMedia() {
            var s = statementOn();
            s.alterarPrioridade(Prioridade.ALTA);
            s.reduzirPrioridade();
            assertEquals(Prioridade.MEDIA, s.getPriority());
        }

        @Test
        @DisplayName("getPesoPrioridade() deve retornar 1 para MEDIA")
        void getPeso_media() {
            assertEquals(1, statementOn().getPesoPrioridade());
        }
    }

    // ─── apagarAnuncio() ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("apagarAnuncio()")
    class ApagarAnuncio {

        @Test
        @DisplayName("deve mudar status para OFF quando ON")
        void on_paraOff() {
            var s = statementOn();
            s.apagarAnuncio();
            assertEquals(Status.OFF, s.getStatus());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException quando status já é OFF")
        void jaOff_lancaExcecao() {
            var s = statementOff();
            var ex = assertThrows(InvalidDataException.class, s::apagarAnuncio);
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("status não deve ser alterado quando já OFF e lança exceção")
        void naoAlteraEmCasoDeExcecao() {
            var s = statementOff();
            assertThrows(InvalidDataException.class, s::apagarAnuncio);
            assertEquals(Status.OFF, s.getStatus());
        }
    }
}