package com.etec.zl.conecta.Application.DTOs.Statements;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.*;

import java.util.List;

public record DTOAnuncio(String title, String content, Midia midia, Prioridade priority, TargetType targetType, List<String> targetsId){
}
