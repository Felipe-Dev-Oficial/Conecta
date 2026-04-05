package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Ports.Input.Users.AlunoBuscaProfessorPorIdPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlunoBuscaProfessorPorIdUseCase implements AlunoBuscaProfessorPorIdPort {

    private static final Logger log = LoggerFactory.getLogger(AlunoBuscaProfessorPorIdUseCase.class);

    private final UserRepository repository;
    private final TryGetByUserService service;
    private final UserMapper mapper;

    public AlunoBuscaProfessorPorIdUseCase(UserRepository repository, TryGetByUserService service, UserMapper mapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public DTORetornoNormal alunoBuscaProfessorPorId(String id_aluno, String id_professor) {
        return mapper.toDTOReturn(service.execute(()-> repository.findByIdProfessores(id_aluno, id_professor), log));
    }
}
