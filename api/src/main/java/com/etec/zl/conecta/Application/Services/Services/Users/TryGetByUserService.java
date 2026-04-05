package com.etec.zl.conecta.Application.Services.Services.Users;

import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class TryGetByUserService {

    public User execute(Supplier<Optional<User>> getMethod, Logger log){
        return TryGetByService.execute(
                getMethod,
                ()-> new UserNotFoundException(),
                log
        );
    }
}
