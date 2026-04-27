package com.etec.zl.conecta.Domain.Entities.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.*;

import java.time.Instant;
import java.util.UUID;

public class Statement {

    private UUID id;
    private String idSender;
    private String title;
    private Instant timestamp;
    private String content;
    private Midia midia;
    private Prioridade priority;
    private boolean edited;
    private Status status;
    private TargetVO targetVO;

    public Statement(UUID id, String idSender, String title, Instant timestamp, String content, Midia midia, Prioridade priority, boolean edited, Status status, TargetVO targetVO) {
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

    public Statement(String idSender, String title, String content, Midia midia, Prioridade priority, TargetVO targetVO) {
        if ((title == null || title.isEmpty()) && (content == null || content.isEmpty()) && midia == null) {
            throw new ProcessingErrorException("Você não pode criar um anúncio vazio");
        }
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

    public void alterarTitulo(String titulo) {
        if (titulo != null && !titulo.isEmpty()) {
            this.title = titulo;
            this.edited = true;
        }
    }

    public void alterarConteudo(String content) {
        if (content != null && !content.isEmpty()) {
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

    public String getTitle() {
        return title;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getContent() {
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
