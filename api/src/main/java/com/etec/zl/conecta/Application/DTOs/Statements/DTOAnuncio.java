package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.ValueObjects.*;

import java.util.List;
import java.util.UUID;

public record DTOAnuncio(Content title, Content content, Midia midia, Prioridade priority, TargetType targetType, List<UUID> targetsId){
}
