package com.etec.zl.conecta.Domain.Entities.FAQs;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
import com.etec.zl.conecta.Domain.ValueObjects.StatusFAQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public class FAQ {

    private static final Logger log = LoggerFactory.getLogger(FAQ.class);

    private UUID id;
    private String authorId;
    private String question;
    private String answer;
    private StatusFAQ statusFAQ;
    private Instant createdAt;
    private Instant updatedAt;
    private Prioridade relevance;

    public FAQ(UUID id, String question, String answer, String authorId, StatusFAQ statusFAQ, Instant createdAt, Instant updatedAt, Prioridade relevance) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.authorId = authorId;
        this.statusFAQ = statusFAQ;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.relevance = relevance;
    }

    public FAQ(String authorId, String question, String answer, Prioridade relevance) {
        this.id = UUID.randomUUID();
        this.authorId = authorId;
        this.question = question;
        this.answer = answer;
        this.statusFAQ = StatusFAQ.RASCUNHO;
        this.createdAt = Instant.now();
        this.updatedAt = null;
        this.relevance = relevance;
    }

    public FAQ() {
    }

    public void publicar(){
        if (this.question == null || this.question.isEmpty()||
            this.answer == null || this.answer.isEmpty() ||
            this.statusFAQ == null
        ) loggar("Preencha todos os dados para enviar");
        if (this.statusFAQ == StatusFAQ.PUBLICADO)loggar("Anuncio já publicado");
        this.statusFAQ = StatusFAQ.PUBLICADO;
    }

    public void alterarPergunta(String pergunta){
        if (pergunta != null && !pergunta.isEmpty()) this.question = pergunta;
    }

    public void alterarResposta(String resposta){
        if (resposta != null && !resposta.isEmpty()) this.answer = resposta;
    }

    public int getPesoPrioridade(){
        return this.relevance.getPeso();
    }

    public void elevarPrioridade(){
        this.relevance.elevar();
    }

    public void reduzirPrioridade(){
        this.relevance.reduzir();
    }

    public void apagarFAQ() {
        this.statusFAQ = StatusFAQ.APAGADO;
    }

    private void loggar(String resposta){
        var e = new InvalidDataException(resposta);
        log.warn(e.getMessage(), e);
        throw e;
    }

    public UUID getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public StatusFAQ getStatusFAQ() {
        return statusFAQ;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Prioridade getRelevance() {
        return relevance;
    }
}
