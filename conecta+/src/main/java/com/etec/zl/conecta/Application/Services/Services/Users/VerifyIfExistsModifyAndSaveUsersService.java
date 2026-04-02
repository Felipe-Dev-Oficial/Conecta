package com.etec.zl.conecta.Application.Services.Services.Users;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.VerifyIfExistsModifyAndSaveService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class VerifyIfExistsModifyAndSaveUsersService {

    private final UserRepository repository;

    public VerifyIfExistsModifyAndSaveUsersService(UserRepository repository) {
        this.repository = repository;
    }

    public void execute(String id, Logger logger, Consumer<User> modifyMethod) {
        VerifyIfExistsModifyAndSaveService.execute(
                ()-> repository.findById(id),
                modifyMethod,
                ()-> new UserNotFoundException(),
                repository::save,
                logger
        );
    }
}
