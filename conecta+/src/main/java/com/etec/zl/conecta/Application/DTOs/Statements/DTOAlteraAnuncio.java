package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;

public record DTOAlteraAnuncio(Content title, Content content, Midia midia, Prioridade priority) {
}
