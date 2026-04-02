package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Ports.Input.Users.SecretariaBuscaPorIdPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserSecretariaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretariaBuscaPorIdUseCase implements SecretariaBuscaPorIdPort {

    private static final Logger log = LoggerFactory.getLogger(SecretariaBuscaPorIdUseCase.class);

    private final UserRepository repository;
    private final TryGetByUserSecretariaService service;

    public SecretariaBuscaPorIdUseCase(UserRepository repository, TryGetByUserSecretariaService service) {
        this.repository = repository;
        this.service = service;
    }


    @Override
    public DTORetornoSecretaria secretariaBuscaPorId(String id) {
        return service.execute(()-> repository.findById(id), log);
    }
}
