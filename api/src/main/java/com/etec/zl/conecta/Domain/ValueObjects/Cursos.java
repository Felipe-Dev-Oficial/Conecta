package com.etec.zl.conecta.Domain.ValueObjects;

import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters.TurmaRepositoryAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Cursos {

    MATUTINO_ADMINISTRACAO_MTEC("ADM-MTEC-M"),
    MATUTINO_CONTABILIDADE_MTEC("CONT-MTEC-M"),
    MATUTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC("DS-MTEC-M"),
    MATUTINO_LOGISTICA_MTEC("LOG-MTEC-M"),
    MATUTINO_RECURSOS_HUMANOS_MTEC("RH-MTEC-M"),

    VESPERTINO_ADMINISTRACAO_MTEC("ADM-MTEC-V"),
    VESPERTINO_CONTABILIDADE_MTEC("CONT-MTEC-V"),
    VESPERTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC("DS-MTEC-V"),
    VESPERTINO_LOGISTICA_MTEC("LOG-MTEC-V"),
    VESPERTINO_RECURSOS_HUMANOS_MTEC("RH-MTEC-V"),

    LOGISTICA_MTEC_N("LOG-MTEC-N"),
    DESENVOLVIMENTO_DE_SISTEMAS("DS-N"),
    ADMINISTRACAO("ADM-N"),
    CONTABILIDADE("CONT-N"),
    LOGISTICA("LOG-N"),
    SERVICOS_JURIDICOS("JUR-N"),

    DESENVOLVIMENTO_DE_SISTEMAS_AMS("DS-AMS");

    private static final Logger log = LoggerFactory.getLogger(TurmaRepositoryAdapter.class);

    private final String sigla;

    Cursos(String sigla) {
        this.sigla = sigla;
    }

    public String getSigla() {
        return sigla;
    }

    public int getModulosTotais(){
        if (this.sigla.contains("MTEC")) {
            return 6;
        } else if (this.sigla.contains("-N")) {
            return 3;
        } else if (this.sigla.contains("AMS")) {
            return 12;
        } else {
            var e = new InvalidDataException("Erro durante processamento da turma " + this.sigla);
            log.warn(e.getMessage(), e);
            throw e;
        }
    }
}