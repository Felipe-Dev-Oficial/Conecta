package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Ports.Input.Users.ProfessorListagemPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfessorListagemUseCase implements ProfessorListagemPort {

    private static final Logger log = LoggerFactory.getLogger(ProfessorListagemUseCase.class);

    private final UserRepository repository;
    private final TryGetUsersService service;

    public ProfessorListagemUseCase(UserRepository repository, TryGetUsersService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public PageResult<DTORetornoNormal> findAllAlunos(String id, PageRequest pageable) {
        return service.execute(()-> repository.findAllAlunos(id, pageable), log);
    }
}
