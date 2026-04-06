package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Ports.Input.Statements.LerAnuncioPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class LerAnuncioUseCase implements LerAnuncioPort {

    private static final Logger log = LoggerFactory.getLogger(LerAnuncioUseCase.class);

    private final StatementRepository repository;
    private final UserRepository userRepository;
    private final StatementMapper mapper;
    private final TryGetByUserService userService;

    public LerAnuncioUseCase(StatementRepository repository, UserRepository userRepository, StatementMapper mapper, TryGetByUserService userService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.userService = userService;
    }


    @Override
    public PageResult<DTORetornoAnuncio> lerAnuncios(String reader_id, PageRequest pageable) {
        User user = userService.execute(
                () -> userRepository.findById(reader_id),
                log
        );
        List<UUID> turmas = user.getTurmasIds() != null ? user.getTurmasIds() : List.of();

        return TryGetService.execute(
                () -> repository.findStatements(mapper.toDTOLeitura(user.getTipo(), turmas), pageable),
                log
        ).map(s -> {
            var publisherName = userService.execute(
                    ()-> userRepository.findById(s.getIdSender()),
                    log
            ).getNome();
            return mapper.toDTOReturn(s, publisherName);
        });
    }
}
