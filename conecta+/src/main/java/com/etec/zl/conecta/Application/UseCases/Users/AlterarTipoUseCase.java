package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.AlterarTipoPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlterarTipoUseCase implements AlterarTipoPort {

    private final Logger log = LoggerFactory.getLogger(AlterarTipoUseCase.class);

    private final UserRepository repository;
    private final VerifyIfExistsModifyAndSaveUsersService service;

    public AlterarTipoUseCase(UserRepository repository, VerifyIfExistsModifyAndSaveUsersService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void alterarTipo(String id, Tipo tipo) {
        service.execute(id, log, u -> u.alteraTipo(tipo));
    }
}
