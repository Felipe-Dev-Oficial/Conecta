package com.etec.zl.conecta.Domain.Entities.Solicitations;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.TypeRequirement;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Solicitation {

    private UUID id;
    private TypeRequirement typeRequirement;
    private String otherRequirement;
    private boolean solved;
    private String idSoliciter;
    private Name nome;
    private Email emailSoliciter;
    private List<String> idCursos;
    private Instant createdAt;

    public void solveRequirement(){
        if (this.solved == true) throw new InvalidDataException("Requerimento já resolvido");
        this.solved = true;
    }

    public Solicitation(UUID id, TypeRequirement typeRequirement, String otherRequirement, boolean solved, String idSoliciter, Name nome, Email emailSoliciter, List<String> idCursos, Instant createdAt) {
        this.id = id;
        this.typeRequirement = typeRequirement;
        this.otherRequirement = (typeRequirement == TypeRequirement.OUTRO) ? otherRequirement : null;
        this.solved = solved;
        this.idSoliciter = idSoliciter;
        this.nome = nome;
        this.emailSoliciter = emailSoliciter;
        this.idCursos = idCursos;
        this.createdAt = createdAt;
    }

    public Solicitation(TypeRequirement typeRequirement, String otherRequirement, String idSoliciter, Name nome, Email emailSoliciter, List<String> idCursos) {
        this.id = UUID.randomUUID();
        this.typeRequirement = typeRequirement;
        this.otherRequirement = otherRequirement;
        this.solved = false;
        this.idSoliciter = idSoliciter;
        this.nome = nome;
        this.emailSoliciter = emailSoliciter;
        this.idCursos = idCursos;
        this.createdAt = Instant.now();
    }

    public Solicitation() {
    }

    public UUID getId() {
        return id;
    }

    public TypeRequirement getTypeRequirement() {
        return typeRequirement;
    }

    public String getOtherRequirement() {
        return otherRequirement;
    }

    public boolean isSolved() {
        return solved;
    }

    public String getIdSoliciter() {
        return idSoliciter;
    }

    public Name getNome() {
        return nome;
    }

    public Email getEmailSoliciter() {
        return emailSoliciter;
    }

    public List<String> getIdCursos() {
        return idCursos;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}