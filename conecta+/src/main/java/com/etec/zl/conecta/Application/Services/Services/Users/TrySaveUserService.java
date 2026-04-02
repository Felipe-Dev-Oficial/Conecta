package com.etec.zl.conecta.Application.Services.Services.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;
import org.slf4j.Logger;

public class TrySaveUserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final TurmaRepository turmaRepository;

    public TrySaveUserService(UserRepository repository, UserMapper mapper, TurmaRepository turmaRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.turmaRepository = turmaRepository;
    }

    public void execute(DTOCadastro dto, Logger log) {

        if (dto.tipo() == Tipo.PROFESSOR || dto.tipo() == Tipo.ALUNO){
            dto.turmas()
                    .forEach(t -> {
                        TryGetByService.execute(
                            ()-> turmaRepository.findById(t),
                            ()-> new InvalidDataException("turma não encontrada"),
                            log
                    );
                    });
        }
        TrySaveService.execute(mapper.toRegister(dto), repository::save, log);
    }
}
