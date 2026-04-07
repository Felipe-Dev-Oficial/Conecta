package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.ValueObjects.Tipo;

import java.util.List;

public record DTOLeitura(Tipo tipo, List<String> turmas) {
}
