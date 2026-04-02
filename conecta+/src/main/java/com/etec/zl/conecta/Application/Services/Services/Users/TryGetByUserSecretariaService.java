package com.etec.zl.conecta.Application.Services.Services.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class TryGetByUserSecretariaService {

    private final UserMapper mapper;

    public TryGetByUserSecretariaService(UserMapper mapper) {
        this.mapper = mapper;
    }

    public DTORetornoSecretaria execute(Supplier<Optional<User>> getMethod, Logger log){
        return mapper.toDTOReturnSecretaria(TryGetByService.execute(
                getMethod,
                ()-> new UserNotFoundException(),
                log
        ));
    }
}
