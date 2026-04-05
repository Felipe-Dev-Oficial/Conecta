package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.Ports.Input.Turmas.EncontraTurmaPorIdPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class EncontrarTurmaPorIdUseCase implements EncontraTurmaPorIdPort {

    private static final Logger log = LoggerFactory.getLogger(EncontrarTurmaPorIdUseCase.class);

    private final TurmaRepository repository;

    public EncontrarTurmaPorIdUseCase(TurmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Turma findTurmaPorId(UUID id) {
        return TryGetByService.execute(
                ()-> repository.findById(id),
                ()-> new InvalidDataException("Nenhuma turma encontrada"),
                log);
    }
}
