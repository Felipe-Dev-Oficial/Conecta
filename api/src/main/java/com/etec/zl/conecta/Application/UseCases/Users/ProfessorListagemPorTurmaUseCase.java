package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Ports.Input.Users.ProfessorListagemPorTurmaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ProfessorListagemPorTurmaUseCase implements ProfessorListagemPorTurmaPort {

    private static final Logger log = LoggerFactory.getLogger(ProfessorListagemPorTurmaUseCase.class);

    private final UserRepository repository;
    private final TryGetUsersService service;

    public ProfessorListagemPorTurmaUseCase(UserRepository repository, TryGetUsersService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public PageResult<DTORetornoNormal> professorListagemPorTurma(String id_professor, UUID id_turma, PageRequest pageable) {
        return service.execute(()-> repository.findAllAlunosByTurma(id_professor, id_turma, pageable), log);
    }
}
