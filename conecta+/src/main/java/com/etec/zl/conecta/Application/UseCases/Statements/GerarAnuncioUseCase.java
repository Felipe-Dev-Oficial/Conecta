package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Ports.Input.Statements.GerarAnuncioPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GerarAnuncioUseCase implements GerarAnuncioPort {

    private static final Logger log = LoggerFactory.getLogger(GerarAnuncioUseCase.class);

    private final StatementRepository repository;
    private final StatementMapper mapper;

    public GerarAnuncioUseCase(StatementRepository repository, StatementMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void gerarAnuncio(String id, DTOAnuncio dto) {
        TrySaveService.execute(mapper.toStatement(id, dto), repository::save, log);
    }

}
