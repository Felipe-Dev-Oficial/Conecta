package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.Ports.Input.Turmas.ListarTodasAsTurmasAtuaisPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarTodasAsTurmasAtuaisUseCase implements ListarTodasAsTurmasAtuaisPort {

    private static final Logger log = LoggerFactory.getLogger(ListarTodasAsTurmasAtuaisUseCase.class);

    private final TurmaRepository repository;

    public ListarTodasAsTurmasAtuaisUseCase(TurmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<Turma> findAllTurmasAtuais(PageRequest pageable) {
        return TryGetService.execute(()-> repository.findAllTurmasAtuais(pageable), log);
    }
}
