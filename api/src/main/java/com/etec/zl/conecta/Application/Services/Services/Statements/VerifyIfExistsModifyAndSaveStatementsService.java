package com.etec.zl.conecta.Application.Services.Services.Statements;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Services.Utilities.VerifyIfExistsModifyAndSaveService;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Consumer;

public class VerifyIfExistsModifyAndSaveStatementsService {

    private final StatementRepository repository;

    public VerifyIfExistsModifyAndSaveStatementsService(StatementRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID statementId, Consumer<Statement> modifierMethod, Logger log) {
        VerifyIfExistsModifyAndSaveService.execute(
                ()-> repository.findById(statementId),
                modifierMethod,
                ()-> new InvalidDataException("Nenhum anuncio com id " + statementId + " encontrado"),
                repository::save,
                log
        );
    }
}
