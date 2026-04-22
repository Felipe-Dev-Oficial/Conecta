package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.VincularNotificadorPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VincularNotificadorUseCase implements VincularNotificadorPort {

    private static final Logger log = LoggerFactory.getLogger(VincularNotificadorUseCase.class);

    private final UserRepository repository;
    private final TryGetByUserService tryGetByUserService;

    public VincularNotificadorUseCase(UserRepository repository, TryGetByUserService tryGetByUserService) {
        this.repository = repository;
        this.tryGetByUserService = tryGetByUserService;
    }

    @Override
    public void vincular(String userId, String endpoint, String p256dh, String auth) {
        tryGetByUserService.execute(
                ()-> repository.findById(userId),
                log
        );
        repository.saveNotificador(userId, endpoint, p256dh, auth);
    }
}
