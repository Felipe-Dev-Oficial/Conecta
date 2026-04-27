package com.etec.zl.conecta.Application.DTOs.Messages;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.Name;

import java.time.Instant;
import java.util.UUID;
public record DTOReturnMessageSecretaria(UUID id, Name nomeSender, String idSender, Name nomeReceiver, String idReceiver, Instant timestamp, String content, Midia midia) {
}
