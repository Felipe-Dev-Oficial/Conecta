package com.etec.zl.conecta.Domain.ValueObjects;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record TargetVO(TargetType targetType, List<String> targetIds) {

    public TargetVO {
        if (targetIds == null) {
            targetIds = Collections.emptyList();
        }
    }

    public static TargetVO paraTodos() {
        return new TargetVO(TargetType.GERAL, null);
    }

    public static TargetVO paraProfessores() {
        return new TargetVO(TargetType.PROFESSORES, null);
    }

    public static TargetVO paraTurma(String idTurma) {
        return new TargetVO(TargetType.TURMA, List.of(idTurma));
    }

    public static TargetVO paraListaDeTurmas(List<String> ids) {
        return new TargetVO(TargetType.TURMAS, ids);
    }
}
