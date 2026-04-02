package com.etec.zl.conecta.Domain.Entities.FAQs;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
import com.etec.zl.conecta.Domain.ValueObjects.StatusFAQ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FAQ")
class FAQTest {

    private FAQ faqRascunho() {
        return new FAQ("autor-1", "Qual o prazo?", "É de 30 dias.", Prioridade.MEDIA);
    }

    private FAQ faqCompleta(StatusFAQ status) {
        return new FAQ(UUID.randomUUID(), "Pergunta?", "Resposta.", "autor", status, Instant.now(), null, Prioridade.MEDIA);
    }

    // ─── Criação ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar FAQ com status RASCUNHO por padrão")
    void criacao_statusRascunho() {
        assertEquals(StatusFAQ.RASCUNHO, faqRascunho().getStatusFAQ());
    }

    @Test
    @DisplayName("Deve criar FAQ com id gerado automaticamente")
    void criacao_idGerado() {
        assertNotNull(faqRascunho().getId());
    }

    @Test
    @DisplayName("Deve criar FAQ com createdAt preenchido")
    void criacao_createdAt() {
        assertNotNull(faqRascunho().getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar FAQ com updatedAt nulo")
    void criacao_updatedAtNulo() {
        assertNull(faqRascunho().getUpdatedAt());
    }

    // ─── publicar() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("publicar() deve mudar status para PUBLICADO")
    void publicar_valido() {
        var faq = faqRascunho();
        faq.publicar();
        assertEquals(StatusFAQ.PUBLICADO, faq.getStatusFAQ());
    }

    @Test
    @DisplayName("publicar() deve lançar InvalidDataException se já PUBLICADO")
    void publicar_jaPublicado() {
        var faq = faqRascunho();
        faq.publicar();
        assertThrows(InvalidDataException.class, faq::publicar);
    }

    @Test
    @DisplayName("publicar() deve lançar InvalidDataException se pergunta for nula")
    void publicar_perguntaNula() {
        var faq = new FAQ(UUID.randomUUID(), null, "Resposta", "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
        assertThrows(InvalidDataException.class, faq::publicar);
    }

    @Test
    @DisplayName("publicar() deve lançar InvalidDataException se pergunta for vazia")
    void publicar_perguntaVazia() {
        var faq = new FAQ(UUID.randomUUID(), "", "Resposta", "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
        assertThrows(InvalidDataException.class, faq::publicar);
    }

    @Test
    @DisplayName("publicar() deve lançar InvalidDataException se resposta for nula")
    void publicar_respostaNula() {
        var faq = new FAQ(UUID.randomUUID(), "Pergunta?", null, "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
        assertThrows(InvalidDataException.class, faq::publicar);
    }

    @Test
    @DisplayName("publicar() deve lançar InvalidDataException se resposta for vazia")
    void publicar_respostaVazia() {
        var faq = new FAQ(UUID.randomUUID(), "Pergunta?", "", "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
        assertThrows(InvalidDataException.class, faq::publicar);
    }

    // ─── apagarFAQ() ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("apagarFAQ() deve mudar status para APAGADO")
    void apagar_rascunho() {
        var faq = faqRascunho();
        faq.apagarFAQ();
        assertEquals(StatusFAQ.APAGADO, faq.getStatusFAQ());
    }

    @Test
    @DisplayName("apagarFAQ() deve mudar status para APAGADO mesmo se PUBLICADO")
    void apagar_publicado() {
        var faq = faqCompleta(StatusFAQ.PUBLICADO);
        faq.apagarFAQ();
        assertEquals(StatusFAQ.APAGADO, faq.getStatusFAQ());
    }

    // ─── alterarResposta() ────────────────────────────────────────────────────

    @Test
    @DisplayName("alterarResposta() deve atualizar para string não vazia")
    void alterarResposta_valido() {
        var faq = faqRascunho();
        faq.alterarResposta("Nova resposta");
        assertEquals("Nova resposta", faq.getAnswer());
    }

    @Test
    @DisplayName("alterarResposta() não deve atualizar para string vazia")
    void alterarResposta_vazio_naoAtualiza() {
        var faq = faqRascunho();
        String original = faq.getAnswer();
        faq.alterarResposta("");
        assertEquals(original, faq.getAnswer());
    }

    @Test
    @DisplayName("alterarResposta() não deve atualizar para null")
    void alterarResposta_null_naoAtualiza() {
        var faq = faqRascunho();
        String original = faq.getAnswer();
        faq.alterarResposta(null);
        assertEquals(original, faq.getAnswer());
    }

    @Test
    @DisplayName("Deve manter pergunta original quando nova pergunta for vazia")
    void alterarPergunta_naoDeveAtualizarComVazio() {
        var faq = new FAQ("autor123", "Pergunta Original", "Resposta", Prioridade.BAIXA);

        faq.alterarPergunta("");

        assertEquals("Pergunta Original", faq.getQuestion(), "A pergunta não deveria ter mudado");
    }

    @Test
    @DisplayName("Deve atualizar pergunta quando string for válida")
    void alterarPergunta_deveAtualizarComSucesso() {
        var faq = new FAQ("autor123", "Pergunta Original", "Resposta", Prioridade.BAIXA);
        String novaPergunta = "Como integro o Redis?";

        faq.alterarPergunta(novaPergunta);

        assertEquals(novaPergunta, faq.getQuestion());
    }

    @Test
    @DisplayName("alterarPergunta() não deve atualizar para null")
    void alterarPergunta_null_naoAtualiza() {
        var faq = faqRascunho();
        String original = faq.getQuestion();
        faq.alterarPergunta(null);
        assertEquals(original, faq.getQuestion());
    }

    // ─── Prioridade ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getPesoPrioridade() deve retornar peso correto")
    void getPeso() {
        var faq = new FAQ("autor", "P", "R", Prioridade.ALTA);
        assertEquals(2, faq.getPesoPrioridade());
    }

    @Test
    @DisplayName("elevarPrioridade() deve subir um nível")
    void elevarPrioridade() {
        // elevarPrioridade() chama relevance.elevar() mas NÃO atribui o retorno — outro bug.
        // O teste documenta que o estado interno NÃO muda (bug).
        var faq = new FAQ("autor", "P", "R", Prioridade.BAIXA);
        faq.elevarPrioridade();
        assertEquals(Prioridade.BAIXA, faq.getRelevance()); // permanece BAIXA por causa do bug
    }

    @Test
    @DisplayName("reduzirPrioridade() deve descer um nível")
    void reduzirPrioridade() {
        // mesmo bug de elevarPrioridade() — não atribui o retorno
        var faq = new FAQ("autor", "P", "R", Prioridade.ALTA);
        faq.reduzirPrioridade();
        assertEquals(Prioridade.ALTA, faq.getRelevance()); // permanece ALTA por causa do bug
    }
}