package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Ports.Input.Statements.LerAnunciosGeraisPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LerAnunciosGeraisUseCase implements LerAnunciosGeraisPort {

    private static final Logger log = LoggerFactory.getLogger(LerAnunciosGeraisUseCase.class);

    private final StatementMapper mapper;
    private final TryGetByUserService userService;
    private final StatementRepository repository;
    private final UserRepository userRepository;

    public LerAnunciosGeraisUseCase(StatementMapper mapper, TryGetByUserService userService, StatementRepository repository, UserRepository userRepository) {
        this.mapper = mapper;
        this.userService = userService;
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public PageResult<DTORetornoAnuncio> lerAnuncios(PageRequest pageable) {
        return TryGetService.execute(
                () -> repository.findGeneralStatements(pageable)
                        .map(s -> {
                            var publisherName = userService.execute(
                                    ()-> userRepository.findById(s.getIdSender()),
                                    log
                            ).getNome();
                            return mapper.toDTOReturn(s, publisherName);
                        }),
                log
        );
    }
}
