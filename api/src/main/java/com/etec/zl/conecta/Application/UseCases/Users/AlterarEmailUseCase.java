package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.AlterarEmailPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AlterarEmailUseCase implements AlterarEmailPort {

    private static final Logger log = LoggerFactory.getLogger(AlterarEmailUseCase.class.getName());

    private final VerifyIfExistsModifyAndSaveUsersService service;

    public AlterarEmailUseCase(VerifyIfExistsModifyAndSaveUsersService service) {
        this.service = service;
    }

    @Override
    public void alterarEmail(String id, UUID token, Email email) {
        service.execute(id, log, u-> u.checkAndChangeEmail(token, email));
    }
}
