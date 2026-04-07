package com.etec.zl.conecta.Domain.Entities.FAQs;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
import com.etec.zl.conecta.Domain.ValueObjects.StatusFAQ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Criação via construtor simples")
    class Criacao {

        @Test
        @DisplayName("deve iniciar com status RASCUNHO")
        void statusRascunho() {
            assertEquals(StatusFAQ.RASCUNHO, faqRascunho().getStatusFAQ());
        }

        @Test
        @DisplayName("deve gerar id não nulo automaticamente")
        void idGerado() {
            assertNotNull(faqRascunho().getId());
        }

        @Test
        @DisplayName("deve preencher createdAt automaticamente")
        void createdAt() {
            assertNotNull(faqRascunho().getCreatedAt());
        }

        @Test
        @DisplayName("deve iniciar com updatedAt nulo")
        void updatedAtNulo() {
            assertNull(faqRascunho().getUpdatedAt());
        }
    }

    // ─── publicar() ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("publicar()")
    class Publicar {

        @Test
        @DisplayName("deve mudar status para PUBLICADO")
        void valido() {
            var faq = faqRascunho();
            faq.publicar();
            assertEquals(StatusFAQ.PUBLICADO, faq.getStatusFAQ());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException se já PUBLICADO")
        void jaPublicado() {
            var faq = faqRascunho();
            faq.publicar();
            var ex = assertThrows(InvalidDataException.class, faq::publicar);
            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("status não deve ser alterado quando já PUBLICADO e lança exceção")
        void naoAlteraEmCasoDeExcecao() {
            var faq = faqRascunho();
            faq.publicar();
            assertThrows(InvalidDataException.class, faq::publicar);
            assertEquals(StatusFAQ.PUBLICADO, faq.getStatusFAQ());
        }

        @Test
        @DisplayName("deve lançar InvalidDataException se pergunta for nula")
        void perguntaNula() {
            var faq = new FAQ(UUID.randomUUID(), null, "Resposta", "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
            assertThrows(InvalidDataException.class, faq::publicar);
        }

        @Test
        @DisplayName("deve lançar InvalidDataException se pergunta for vazia")
        void perguntaVazia() {
            var faq = new FAQ(UUID.randomUUID(), "", "Resposta", "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
            assertThrows(InvalidDataException.class, faq::publicar);
        }

        @Test
        @DisplayName("deve lançar InvalidDataException se resposta for nula")
        void respostaNula() {
            var faq = new FAQ(UUID.randomUUID(), "Pergunta?", null, "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
            assertThrows(InvalidDataException.class, faq::publicar);
        }

        @Test
        @DisplayName("deve lançar InvalidDataException se resposta for vazia")
        void respostaVazia() {
            var faq = new FAQ(UUID.randomUUID(), "Pergunta?", "", "autor", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.BAIXA);
            assertThrows(InvalidDataException.class, faq::publicar);
        }
    }

    // ─── apagarFAQ() ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("apagarFAQ()")
    class ApagarFAQ {

        @Test
        @DisplayName("deve mudar status para APAGADO quando RASCUNHO")
        void rascunho_paraApagado() {
            var faq = faqRascunho();
            faq.apagarFAQ();
            assertEquals(StatusFAQ.APAGADO, faq.getStatusFAQ());
        }

        @Test
        @DisplayName("deve mudar status para APAGADO mesmo quando PUBLICADO")
        void publicado_paraApagado() {
            var faq = faqCompleta(StatusFAQ.PUBLICADO);
            faq.apagarFAQ();
            assertEquals(StatusFAQ.APAGADO, faq.getStatusFAQ());
        }
    }

    // ─── alterarResposta() ────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarResposta()")
    class AlterarResposta {

        @Test
        @DisplayName("deve atualizar para o novo valor quando válido")
        void valido() {
            var faq = faqRascunho();
            faq.alterarResposta("Nova resposta");
            assertEquals("Nova resposta", faq.getAnswer());
        }

        @Test
        @DisplayName("string vazia não deve atualizar a resposta")
        void vazio_naoAtualiza() {
            var faq = faqRascunho();
            String original = faq.getAnswer();
            faq.alterarResposta("");
            assertEquals(original, faq.getAnswer());
        }

        @Test
        @DisplayName("null não deve atualizar a resposta")
        void nulo_naoAtualiza() {
            var faq = faqRascunho();
            String original = faq.getAnswer();
            faq.alterarResposta(null);
            assertEquals(original, faq.getAnswer());
        }
    }

    // ─── alterarPergunta() ────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarPergunta()")
    class AlterarPergunta {

        @Test
        @DisplayName("deve atualizar para o novo valor quando válido")
        void valido() {
            var faq = faqRascunho();
            faq.alterarPergunta("Como integro o Redis?");
            assertEquals("Como integro o Redis?", faq.getQuestion());
        }

        @Test
        @DisplayName("string vazia não deve atualizar a pergunta")
        void vazio_naoAtualiza() {
            var faq = new FAQ("autor123", "Pergunta Original", "Resposta", Prioridade.BAIXA);
            faq.alterarPergunta("");
            assertEquals("Pergunta Original", faq.getQuestion());
        }

        @Test
        @DisplayName("null não deve atualizar a pergunta")
        void nulo_naoAtualiza() {
            var faq = faqRascunho();
            String original = faq.getQuestion();
            faq.alterarPergunta(null);
            assertEquals(original, faq.getQuestion());
        }
    }

    // ─── Prioridade ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Prioridade (relevância)")
    class PrioridadeFAQ {

        @Test
        @DisplayName("getPesoPrioridade() deve retornar 2 para ALTA")
        void getPeso_alta() {
            assertEquals(2, new FAQ("autor", "P", "R", Prioridade.ALTA).getPesoPrioridade());
        }

        @Test
        @DisplayName("getPesoPrioridade() deve retornar 1 para MEDIA")
        void getPeso_media() {
            assertEquals(1, new FAQ("autor", "P", "R", Prioridade.MEDIA).getPesoPrioridade());
        }

        @Test
        @DisplayName("elevarPrioridade() deve subir BAIXA para MEDIA")
        void elevar_baixa_paraMedia() {
            var faq = new FAQ("autor", "P", "R", Prioridade.BAIXA);
            faq.elevarPrioridade();
            assertEquals(Prioridade.MEDIA, faq.getRelevance());
        }

        @Test
        @DisplayName("elevarPrioridade() deve subir MEDIA para ALTA")
        void elevar_media_paraAlta() {
            var faq = new FAQ("autor", "P", "R", Prioridade.MEDIA);
            faq.elevarPrioridade();
            assertEquals(Prioridade.ALTA, faq.getRelevance());
        }

        @Test
        @DisplayName("elevarPrioridade() deve subir ALTA para URGENTE")
        void elevar_alta_paraUrgente() {
            var faq = new FAQ("autor", "P", "R", Prioridade.ALTA);
            faq.elevarPrioridade();
            assertEquals(Prioridade.URGENTE, faq.getRelevance());
        }

        @Test
        @DisplayName("elevarPrioridade() em URGENTE deve permanecer URGENTE (teto)")
        void elevar_urgente_permanece() {
            var faq = new FAQ("autor", "P", "R", Prioridade.URGENTE);
            faq.elevarPrioridade();
            assertEquals(Prioridade.URGENTE, faq.getRelevance());
        }

        @Test
        @DisplayName("reduzirPrioridade() deve descer URGENTE para ALTA")
        void reduzir_urgente_paraAlta() {
            var faq = new FAQ("autor", "P", "R", Prioridade.URGENTE);
            faq.reduzirPrioridade();
            assertEquals(Prioridade.ALTA, faq.getRelevance());
        }

        @Test
        @DisplayName("reduzirPrioridade() deve descer ALTA para MEDIA")
        void reduzir_alta_paraMedia() {
            var faq = new FAQ("autor", "P", "R", Prioridade.ALTA);
            faq.reduzirPrioridade();
            assertEquals(Prioridade.MEDIA, faq.getRelevance());
        }

        @Test
        @DisplayName("reduzirPrioridade() deve descer MEDIA para BAIXA")
        void reduzir_media_paraBaixa() {
            var faq = new FAQ("autor", "P", "R", Prioridade.MEDIA);
            faq.reduzirPrioridade();
            assertEquals(Prioridade.BAIXA, faq.getRelevance());
        }

        @Test
        @DisplayName("reduzirPrioridade() em BAIXA deve permanecer BAIXA (piso)")
        void reduzir_baixa_permanece() {
            var faq = new FAQ("autor", "P", "R", Prioridade.BAIXA);
            faq.reduzirPrioridade();
            assertEquals(Prioridade.BAIXA, faq.getRelevance());
        }
    }
}