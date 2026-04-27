package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;

public record DTOAlteraAnuncio(String title, String content, Midia midia, Prioridade priority) {
}
