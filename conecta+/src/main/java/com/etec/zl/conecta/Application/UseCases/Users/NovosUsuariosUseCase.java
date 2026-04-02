package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.Ports.Input.Users.NovosUsuariosPort;
import com.etec.zl.conecta.Application.Services.Services.Users.TrySaveUserService;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NovosUsuariosUseCase implements NovosUsuariosPort {

    private static final Logger log = LoggerFactory.getLogger(NovosUsuariosUseCase.class);

    private final TrySaveUserService service;

    public NovosUsuariosUseCase(TrySaveUserService service) {
        this.service = service;
    }

    //pensado em algo como o registro de todos os novos alunos e professores no inicio ou meio do ano
    @Override
    public void novosUsuarios(List<DTOCadastro> usuariosNovos) {

        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        usuariosNovos.parallelStream().forEach(dto -> {
            try {
                service.execute(dto, log);
            } catch (Exception e) {
                errors.add("Falha ao registrar: " + dto.nome().name().toUpperCase() + " - " + e.getMessage());
            }
        });
        if (!errors.isEmpty()) {
            throw new ProcessingErrorException("Falhas ao registrar: " + errors);
        }
    }
}
