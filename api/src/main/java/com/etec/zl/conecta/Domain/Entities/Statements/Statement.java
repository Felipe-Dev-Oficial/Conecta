package com.etec.zl.conecta.Domain.Entities.Statements;

import com.etec.zl.conecta.Domain.ValueObjects.*;

import java.time.Instant;
import java.util.UUID;

public class Statement {

    private UUID id;
    private String idSender;
    private Content title;
    private Instant timestamp;
    private Content content;
    private Midia midia;
    private Prioridade priority;
    private boolean edited;
    private Status status;
    private TargetVO targetVO;

    public Statement(UUID id, String idSender, Content title, Instant timestamp, Content content, Midia midia, Prioridade priority, boolean edited, Status status, TargetVO targetVO) {
        this.id = id;
        this.idSender = idSender;
        this.title = title;
        this.timestamp = timestamp;
        this.content = content;
        this.midia = midia;
        this.priority = priority;
        this.edited = edited;
        this.status = status;
        this.targetVO = targetVO;
    }

    public Statement(String idSender, Content title, Content content, Midia midia, Prioridade priority, TargetVO targetVO) {
        this.id = UUID.randomUUID();
        this.idSender = idSender;
        this.title = title;
        this.timestamp = Instant.now();
        this.content = content;
        this.midia = midia;
        this.priority = priority;
        this.edited = false;
        this.status = Status.ON;
        this.targetVO = targetVO;
    }

    public Statement() {
    }

    public void alterarMidia(Midia midia) {
        if (midia != null && !midia.link().isEmpty()) {
            this.midia = midia;
            this.edited = true;
        }
    }

    public void alterarTitulo(Content titulo) {
        if (titulo != null && !titulo.content().isEmpty()) {
            this.title = titulo;
            this.edited = true;
        }
    }

    public void alterarConteudo(Content content) {
        if (content != null && !content.content().isEmpty()) {
            this.content = content;
            this.edited = true;
        }
    }

    public void alterarPrioridade(Prioridade prioridade) {
        if (prioridade != null) this.priority = prioridade;
    }

    public int getPesoPrioridade(){
        return this.priority.getPeso();
    }

    public void elevarPrioridade(){
        this.priority = this.priority.elevar();
    }
    public void reduzirPrioridade(){
        this.priority = this.priority.reduzir();
    }

    public void apagarAnuncio(){
        this.status = this.status.desativar();
    }

    public UUID getId() {
        return id;
    }

    public String getIdSender() {
        return idSender;
    }

    public Content getTitle() {
        return title;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Content getContent() {
        return content;
    }

    public Midia getMidia() {
        return midia;
    }

    public Prioridade getPriority() {
        return priority;
    }

    public boolean isEdited() {
        return edited;
    }

    public Status getStatus() {
        return status;
    }

    public TargetVO getTargetVO() {
        return targetVO;
    }
}
