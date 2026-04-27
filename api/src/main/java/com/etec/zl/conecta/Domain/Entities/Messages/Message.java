package com.etec.zl.conecta.Domain.Entities.Messages;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;

import java.time.Instant;
import java.util.UUID;

public class Message {

    private UUID id;
    private String idSender;
    private String idReceiver;
    private Instant timestamp;
    private String content;
    private Midia midia;

    public Message(UUID id, String idSender, String idReceiver, Instant timestamp, String content, Midia midia) {
        this.id = id;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.timestamp = timestamp;
        this.content = content;
        this.midia = midia;
    }

    public Message(String idSender, String idReceiver, String content, Midia midia) {
        if ((content == null || content.isEmpty()) && midia == null) throw new ProcessingErrorException("Você não pode enviar uma mensagem vazia");
        this.id = UUID.randomUUID();
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.timestamp = Instant.now();
        this.content = content;
        this.midia = midia;
    }

    public Message() {
    }

    public UUID getId() {
        return id;
    }

    public String getIdSender() {
        return idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
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
}
