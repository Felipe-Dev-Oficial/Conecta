package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Ports.Input.Users.RetornarSecretariaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetornarSecretariaUseCase implements RetornarSecretariaPort {

    private static final Logger log = LoggerFactory.getLogger(RetornarSecretariaUseCase.class);

    private final UserRepository repository;
    private final TryGetUsersService service;

    public RetornarSecretariaUseCase(UserRepository repository, TryGetUsersService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public PageResult<DTORetornoNormal> listagemSecretaria(PageRequest pageRequest) {
        return service.execute(()-> repository.findAllSecretaria(pageRequest), log);
    }
}
