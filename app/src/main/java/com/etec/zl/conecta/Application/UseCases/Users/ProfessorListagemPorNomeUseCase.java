package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Ports.Input.Users.ProfessorListagemPorNomePort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfessorListagemPorNomeUseCase implements ProfessorListagemPorNomePort {

    private static final Logger log = LoggerFactory.getLogger(ProfessorListagemPorNomeUseCase.class);

    private final UserRepository repository;
    private final TryGetUsersService service;

    public ProfessorListagemPorNomeUseCase(UserRepository repository, TryGetUsersService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public PageResult<DTORetornoNormal> professorListagemPorNome(String id, Name name, PageRequest pageable) {
        return service.execute(()-> repository.findAllAlunosByNome(id, name, pageable), log);
    }
}
