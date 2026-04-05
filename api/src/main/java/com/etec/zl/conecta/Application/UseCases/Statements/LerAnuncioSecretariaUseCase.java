package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Ports.Input.Statements.LerAnuncioSecretariaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class LerAnuncioSecretariaUseCase implements LerAnuncioSecretariaPort {

    private static final Logger log = LoggerFactory.getLogger(LerAnuncioSecretariaUseCase.class);

    private final StatementRepository repository;
    private final UserRepository userRepository;
    private final StatementMapper mapper;
    private final TryGetByUserService userService;

    public LerAnuncioSecretariaUseCase(StatementRepository repository, UserRepository userRepository, StatementMapper mapper, TryGetByUserService userService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userService = userService;
    }


    @Override
    public PageResult<Statement> lerAnunciosSecretaria(PageRequest pageable) {
        return TryGetService.execute(() -> repository.findAllStatements(pageable), log);
    }
}
