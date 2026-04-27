package com.etec.zl.conecta.Application.DTOs.Solicitations;

import com.etec.zl.conecta.Domain.ValueObjects.TypeRequirement;

import java.time.Instant;
import java.util.UUID;

public record DTOReturnRequirement(
        UUID id,
        TypeRequirement type,
        String otherRequirement,
        boolean solved,
        Instant createdAt
) {
}