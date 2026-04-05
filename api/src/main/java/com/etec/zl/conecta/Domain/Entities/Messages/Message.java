package com.etec.zl.conecta.Domain.Entities.Messages;

import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.Midia;

import java.time.Instant;
import java.util.UUID;

public class Message {

    private UUID id;
    private String idSender;
    private String idReceiver;
    private Instant timestamp;
    private Content content;
    private Midia midia;

    public Message(UUID id, String idSender, String idReceiver, Instant timestamp, Content content, Midia midia) {
        this.id = id;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.timestamp = timestamp;
        this.content = content;
        this.midia = midia;
    }

    public Message(String idSender, String idReceiver, Content content, Midia midia) {
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

    public Content getContent() {
        return content;
    }

    public Midia getMidia() {
        return midia;
    }
}
