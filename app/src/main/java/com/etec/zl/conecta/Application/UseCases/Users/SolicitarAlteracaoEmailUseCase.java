package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.SolicitarAlteracaoEmailPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.StartChangeService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolicitarAlteracaoEmailUseCase implements SolicitarAlteracaoEmailPort {

    private static final Logger log = LoggerFactory.getLogger(SolicitarAlteracaoEmailUseCase.class);

    private final UserRepository repository;
    private final StartChangeService service;

    public SolicitarAlteracaoEmailUseCase(UserRepository repository, StartChangeService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void solicitarAlteracaoEmail(String id) {
        service.execute(id, User::sendUpdateEmailToken, User::getEmailUpdater, "Alteração de email", log);
    }
}
