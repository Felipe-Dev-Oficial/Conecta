package com.etec.zl.conecta.Domain.Entities.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Deve criar Statement com edited=false por padrão via construtor simples")
    void criacao_editedFalse() {
        var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
        assertFalse(s.isEdited());
    }

    @Test
    @DisplayName("Deve criar Statement com id gerado automaticamente via construtor simples")
    void criacao_idGerado() {
        var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
        assertNotNull(s.getId());
    }

    @Test
    @DisplayName("Deve criar Statement com timestamp preenchido via construtor simples")
    void criacao_timestamp() {
        var s = new Statement("sender", new Content("T"), new Content("C"), null, Prioridade.MEDIA, TargetVO.paraTodos());
        assertNotNull(s.getTimestamp());
    }

    // ─── alterarTitulo() ──────────────────────────────────────────────────────

    @Test
    @DisplayName("alterarTitulo() deve atualizar título e marcar edited=true")
    void alterarTitulo_valido() {
        var s = statementOn();
        s.alterarTitulo(new Content("Novo título"));
        assertEquals("Novo título", s.getTitle().content());
        assertTrue(s.isEdited());
    }

    @Test
    @DisplayName("alterarTitulo() com content vazio não deve atualizar")
    void alterarTitulo_vazio_naoAtualiza() {
        var s = statementOn();
        Content original = s.getTitle();
        s.alterarTitulo(new Content(""));
        assertEquals(original, s.getTitle());
        assertFalse(s.isEdited());
    }

    @Test
    @DisplayName("alterarTitulo() com null não deve atualizar")
    void alterarTitulo_null_naoAtualiza() {
        var s = statementOn();
        Content original = s.getTitle();
        s.alterarTitulo(null);
        assertEquals(original, s.getTitle());
        assertFalse(s.isEdited());
    }

    // ─── alterarConteudo() ────────────────────────────────────────────────────

    @Test
    @DisplayName("alterarConteudo() deve atualizar conteúdo e marcar edited=true")
    void alterarConteudo_valido() {
        var s = statementOn();
        s.alterarConteudo(new Content("Novo conteúdo"));
        assertEquals("Novo conteúdo", s.getContent().content());
        assertTrue(s.isEdited());
    }

    @Test
    @DisplayName("alterarConteudo() com content vazio não deve atualizar")
    void alterarConteudo_vazio_naoAtualiza() {
        var s = statementOn();
        Content original = s.getContent();
        s.alterarConteudo(new Content(""));
        assertEquals(original, s.getContent());
        assertFalse(s.isEdited());
    }

    @Test
    @DisplayName("alterarConteudo() com null não deve atualizar")
    void alterarConteudo_null_naoAtualiza() {
        var s = statementOn();
        Content original = s.getContent();
        s.alterarConteudo(null);
        assertEquals(original, s.getContent());
        assertFalse(s.isEdited());
    }

    // ─── alterarMidia() ───────────────────────────────────────────────────────

    @Test
    @DisplayName("alterarMidia() deve atualizar mídia e marcar edited=true")
    void alterarMidia_valido() {
        var s = statementOn();
        var midia = new Midia(TipoMidia.FOTO, "https://img.com/foto.jpg");
        s.alterarMidia(midia);
        assertEquals(midia, s.getMidia());
        assertTrue(s.isEdited());
    }

    @Test
    @DisplayName("alterarMidia() com link vazio não deve atualizar")
    void alterarMidia_linkVazio_naoAtualiza() {
        var s = statementOn();
        s.alterarMidia(new Midia(TipoMidia.FOTO, ""));
        assertFalse(s.isEdited());
    }

    @Test
    @DisplayName("alterarMidia() com null não deve atualizar")
    void alterarMidia_null_naoAtualiza() {
        var s = statementOn();
        s.alterarMidia(null);
        assertFalse(s.isEdited());
    }

    // ─── alterarPrioridade() ──────────────────────────────────────────────────

    @Test
    @DisplayName("alterarPrioridade() deve atualizar prioridade")
    void alterarPrioridade_valido() {
        var s = statementOn();
        s.alterarPrioridade(Prioridade.URGENTE);
        assertEquals(Prioridade.URGENTE, s.getPriority());
    }

    @Test
    @DisplayName("alterarPrioridade() com null não deve atualizar")
    void alterarPrioridade_null_naoAtualiza() {
        var s = statementOn();
        s.alterarPrioridade(null);
        assertEquals(Prioridade.MEDIA, s.getPriority());
    }

    // ─── elevarPrioridade() / reduzirPrioridade() ─────────────────────────────

    @Test
    @DisplayName("elevarPrioridade() deve subir um nível")
    void elevarPrioridade() {
        var s = statementOn();
        s.elevarPrioridade(); // MEDIA -> ALTA
        assertEquals(Prioridade.ALTA, s.getPriority());
    }

    @Test
    @DisplayName("reduzirPrioridade() deve descer um nível")
    void reduzirPrioridade() {
        var s = statementOn();
        s.reduzirPrioridade(); // MEDIA -> BAIXA
        assertEquals(Prioridade.BAIXA, s.getPriority());
    }

    @Test
    @DisplayName("getPesoPrioridade() deve retornar peso correto")
    void getPeso() {
        assertEquals(1, statementOn().getPesoPrioridade()); // MEDIA = 1
    }

    // ─── apagarAnuncio() ──────────────────────────────────────────────────────

    @Test
    @DisplayName("apagarAnuncio() deve mudar status para OFF")
    void apagarAnuncio_on() {
        var s = statementOn();
        s.apagarAnuncio();
        assertEquals(Status.OFF, s.getStatus());
    }

    @Test
    @DisplayName("apagarAnuncio() deve lançar InvalidDataException se status já for OFF")
    void apagarAnuncio_jaOff() {
        assertThrows(InvalidDataException.class, () -> statementOff().apagarAnuncio());
    }
}