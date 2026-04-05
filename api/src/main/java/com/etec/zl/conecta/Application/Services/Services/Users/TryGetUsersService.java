package com.etec.zl.conecta.Application.Services.Services.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;

import java.util.function.Supplier;

public class TryGetUsersService {

    private final UserMapper mapper;

    public TryGetUsersService(UserMapper mapper) {
        this.mapper = mapper;
    }

    public PageResult<DTORetornoNormal> execute(Supplier<PageResult<User>> getMethod, Logger log){
        return TryGetService.execute(getMethod, log)
                .map(mapper::toDTOReturn);
    }
}
