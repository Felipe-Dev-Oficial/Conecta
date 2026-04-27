package com.etec.zl.conecta.Application.DTOs.Solicitations;

import com.etec.zl.conecta.Domain.ValueObjects.TypeRequirement;

public record DTORequirement(TypeRequirement typeRequirement, String otherRequirement) {
}