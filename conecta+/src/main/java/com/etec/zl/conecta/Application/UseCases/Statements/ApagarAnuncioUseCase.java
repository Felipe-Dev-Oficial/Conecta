package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.Ports.Input.Statements.ApagarAnuncioPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ApagarAnuncioUseCase implements ApagarAnuncioPort {

    private static final Logger log = LoggerFactory.getLogger(ApagarAnuncioUseCase.class);

    private final VerifyIfExistsModifyAndSaveStatementsService service;

    public ApagarAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {

        this.service = service;
    }

    @Override
    public void apagarAnuncio(UUID anuncio_id) {
        service.execute(
                anuncio_id,
                Statement::apagarAnuncio,
                log
        );
    }
}
