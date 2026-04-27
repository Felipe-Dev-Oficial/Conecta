package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.Name;

import java.time.Instant;

public record DTORetornoAnuncio(Name nome, String titulo, String content, Midia midia, Instant publicacao, boolean edited) {
}
