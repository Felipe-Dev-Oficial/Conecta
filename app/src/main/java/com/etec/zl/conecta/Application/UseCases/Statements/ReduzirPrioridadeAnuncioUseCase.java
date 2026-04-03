package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.Ports.Input.Statements.ReduzirPrioridadeAnuncioPort;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ReduzirPrioridadeAnuncioUseCase implements ReduzirPrioridadeAnuncioPort {

    private static final Logger log = LoggerFactory.getLogger(ReduzirPrioridadeAnuncioUseCase.class);

    private final VerifyIfExistsModifyAndSaveStatementsService service;

    public ReduzirPrioridadeAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {
        this.service = service;
    }

    @Override
    public void reduzirPrioridadeAnuncio(UUID anuncio_id) {
        service.execute(
                anuncio_id,
                Statement::reduzirPrioridade,
                log
        );
    }
}
