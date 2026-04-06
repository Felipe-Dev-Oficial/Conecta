package com.etec.zl.conecta.Application.DTOs.Messages;

import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.Entities.Midia.Midia;

public record DTOInfoMessage(Content content, Midia midia) {
}
