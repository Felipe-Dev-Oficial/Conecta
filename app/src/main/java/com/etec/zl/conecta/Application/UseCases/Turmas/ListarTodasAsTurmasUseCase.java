package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.Ports.Input.Turmas.ListarTodasAsTurmasPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarTodasAsTurmasUseCase implements ListarTodasAsTurmasPort {

    private static final Logger log = LoggerFactory.getLogger(ListarTodasAsTurmasUseCase.class);

    private final TurmaRepository repository;

    public ListarTodasAsTurmasUseCase(TurmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<Turma> findAllTurmas(PageRequest pageable) {
        return TryGetService.execute(()-> repository.findAllTurmas(pageable), log);
    }
}
