package com.etec.zl.conecta.Application.DTOs.Messages;

import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.Name;

public record DTOReturnMessage(Name nameSender, Content content, Midia midia) {
}
