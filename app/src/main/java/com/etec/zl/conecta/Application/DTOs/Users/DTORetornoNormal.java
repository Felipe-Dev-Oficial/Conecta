package com.etec.zl.conecta.Application.DTOs.Users;

import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;

public record DTORetornoNormal(
        String id,
        Name nome,
        Tipo tipo
) {
}
