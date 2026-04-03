package com.etec.zl.conecta.Application.DTOs.Users;

import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PhoneNumber;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;

public record DTORetornoSecretaria(
        String id,
        Name nome,
        Email email,
        PhoneNumber numero,
        Tipo tipo
) {
}
