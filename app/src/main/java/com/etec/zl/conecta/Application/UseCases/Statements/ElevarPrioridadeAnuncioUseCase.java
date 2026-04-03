package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.Ports.Input.Statements.ElevarPrioridadeAnuncioPort;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ElevarPrioridadeAnuncioUseCase implements ElevarPrioridadeAnuncioPort {

    private static final Logger log = LoggerFactory.getLogger(ElevarPrioridadeAnuncioUseCase.class);

    private final VerifyIfExistsModifyAndSaveStatementsService service;

    public ElevarPrioridadeAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service){
        this.service = service;
    }

    @Override
    public void elevarPrioridadeAnuncio(UUID anuncio_id) {
        service.execute(
                anuncio_id,
                Statement::elevarPrioridade,
                log
        );
    }
}
