package com.etec.zl.conecta.Application.Services.Services.Users;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.EmailService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.TokenUpdater;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;

public class StartChangeService {

    private final UserRepository repository;
    private final EmailService service;

    public StartChangeService(UserRepository repository, EmailService service) {
        this.repository = repository;
        this.service = service;
    }

    public void execute(
            String id,
            Consumer<User> action,
            Function<User, TokenUpdater> tokenExtractor,
            String emailSubject,
            Logger log
    ){
        var user = repository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        try {
            action.accept(user);

            TokenUpdater updater = tokenExtractor.apply(user);

            if (updater == null || updater.token() == null) {
                var e = new ValidationFailedException("Token was not properly initialized.");
                log.warn(e.getMessage(), e);
                throw e;
            }

            service.send("Seu token de alteração " + updater.token(), user.getEmail(), emailSubject);

            repository.save(user);

        } catch (Exception e) {
            log.error("Failed to start change process for user: {}", user.getId(), e);
            throw new ProcessingErrorException("Error initiating update via email.");
        }
    }
}
