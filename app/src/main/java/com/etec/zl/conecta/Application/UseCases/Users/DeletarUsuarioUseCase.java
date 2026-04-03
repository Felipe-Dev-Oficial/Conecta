package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.DeletarUsuarioPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletarUsuarioUseCase implements DeletarUsuarioPort {

    private final Logger log = LoggerFactory.getLogger(DeletarUsuarioUseCase.class);

    private final VerifyIfExistsModifyAndSaveUsersService service;

    public DeletarUsuarioUseCase(VerifyIfExistsModifyAndSaveUsersService service) {
        this.service = service;
    }

    @Override
    public void deletarUsuario(String id) {
        service.execute(
                id,
                log,
                User::desativa
        );
    }
}
