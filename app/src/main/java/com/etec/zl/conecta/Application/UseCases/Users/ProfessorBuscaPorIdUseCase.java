package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Ports.Input.Users.ProfessorBuscaPorIdPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfessorBuscaPorIdUseCase implements ProfessorBuscaPorIdPort {

    private static final Logger log = LoggerFactory.getLogger(ProfessorBuscaPorIdUseCase.class);

    private final UserRepository repository;
    private final TryGetByUserService service;
    private final UserMapper mapper;

    public ProfessorBuscaPorIdUseCase(UserRepository repository, TryGetByUserService service, UserMapper mapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public DTORetornoNormal professorBuscaPorId(String id_professor, String id_aluno) {
        return mapper.toDTOReturn(service.execute(()-> repository.findByIdAlunos(id_professor, id_aluno), log));
    }
}
