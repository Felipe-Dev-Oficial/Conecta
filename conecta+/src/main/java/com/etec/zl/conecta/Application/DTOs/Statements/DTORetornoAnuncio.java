package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.Name;

import java.time.Instant;

public record DTORetornoAnuncio(Name nome, Content titulo, Content content, Instant publicacao, boolean edited) {
}
