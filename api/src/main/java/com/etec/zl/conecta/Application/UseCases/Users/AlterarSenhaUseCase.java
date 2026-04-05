package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.AlterarSenhaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AlterarSenhaUseCase implements AlterarSenhaPort {

    private static final Logger log = LoggerFactory.getLogger(AlterarSenhaUseCase.class.getName());

    private final VerifyIfExistsModifyAndSaveUsersService service;

    public AlterarSenhaUseCase(VerifyIfExistsModifyAndSaveUsersService service) {
        this.service = service;
    }

    @Override
    public void alterarSenha(String id, UUID token, Password senha) {
        service.execute(id, log, u-> u.checkAndChangePassword(token, senha));
    }
}
