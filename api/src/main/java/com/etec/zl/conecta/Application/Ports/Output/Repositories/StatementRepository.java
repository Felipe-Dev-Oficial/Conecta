package com.etec.zl.conecta.Application.Ports.Output.Repositories;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface StatementRepository {

    void save(Statement statement);
    Optional<Statement> findById(UUID id);
    PageResult<Statement> findAllStatements(PageRequest pageable);
    PageResult<Statement> findStatements(DTOLeitura dto, PageRequest pageable);
}
