package com.etec.zl.conecta.Application.DTOs.Messages;

import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.Midia;

import java.util.UUID;

public record DTORegisterMessage(String idSender, String idReceiver, DTOInfoMessage dto) {
}
