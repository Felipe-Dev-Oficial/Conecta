package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Ports.Input.Users.SecretariaListagemPorFuncionariosPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersSecretariaService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretariaListagemPorFuncionariosUseCase implements SecretariaListagemPorFuncionariosPort {

    private static final Logger log = LoggerFactory.getLogger(SecretariaListagemPorFuncionariosUseCase.class);

    private final UserRepository repository;
    private final TryGetUsersSecretariaService service;

    public SecretariaListagemPorFuncionariosUseCase(UserRepository repository, TryGetUsersSecretariaService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public PageResult<DTORetornoSecretaria> secretariaListagemPorFuncionarios(PageRequest pageable) {
        return service.execute(()-> repository.findAllFuncionarios(pageable), log);
    }
}
