package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.ValueObjects.Tipo;

import java.util.List;
import java.util.UUID;

public record DTOLeitura(Tipo tipo, List<UUID> turmas) {
}
