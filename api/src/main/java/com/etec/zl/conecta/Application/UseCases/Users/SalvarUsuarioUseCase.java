package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.Ports.Input.Users.SalvarUsuarioPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Services.Users.TrySaveUserService;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalvarUsuarioUseCase implements SalvarUsuarioPort {


    private static final Logger log = LoggerFactory.getLogger(SalvarUsuarioUseCase.class);

    private final TrySaveUserService service;
    private final UserRepository repository;
    private final TryGetByUserService getService;

    public SalvarUsuarioUseCase(TrySaveUserService service, UserRepository repository, TryGetByUserService getService) {
        this.service = service;
        this.repository = repository;
        this.getService = getService;
    }

    @Override
    public void salvarUsuario(DTOCadastro dto) {
        var userOpt = repository.findByEmail(dto.email());
        if (userOpt.isPresent()){
            var user = userOpt.get();
            user.addTurmas(dto.turmas());
            if (user.getTipo() != dto.tipo()) user.alteraTipo(dto.tipo());
            TrySaveService.execute(user, repository::save, log);
        } else {
            service.execute(dto, log);
        }
    }
}
