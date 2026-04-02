package com.etec.zl.conecta.Application.DTOs.Users;

import com.etec.zl.conecta.Domain.ValueObjects.*;

import java.util.List;
import java.util.UUID;

public record DTOCadastro(
        String id,
        Name nome,
        Email email,
        PhoneNumber numero,
        Password senha,
        Tipo tipo,
        List<UUID> turmas
) {
}
