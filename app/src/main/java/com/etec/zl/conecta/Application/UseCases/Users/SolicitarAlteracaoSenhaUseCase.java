package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.SolicitarAlteracaoSenhaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.StartChangeService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitarAlteracaoSenhaUseCase implements SolicitarAlteracaoSenhaPort {

    private static final Logger log = LoggerFactory.getLogger(SolicitarAlteracaoSenhaUseCase.class);

    private final UserRepository repository;
    private final StartChangeService service;

    public SolicitarAlteracaoSenhaUseCase(UserRepository repository, StartChangeService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void solicitarAlteracaoSenha(String id) {
        service.execute(id, User::sendUpdatePasswordToken, User::getPasswordUpdater, "Alteração de senha", log);
    }
}
