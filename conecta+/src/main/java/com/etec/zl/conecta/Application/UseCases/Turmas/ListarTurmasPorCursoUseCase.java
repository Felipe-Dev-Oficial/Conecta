package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.Ports.Input.Turmas.ListarTurmasPorCursoPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListarTurmasPorCursoUseCase implements ListarTurmasPorCursoPort {

    private static final Logger log = LoggerFactory.getLogger(ListarTurmasPorCursoUseCase.class);

    private final TurmaRepository repository;

    public ListarTurmasPorCursoUseCase(TurmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<Turma> findAllTurmasByCurso(Cursos curso, PageRequest pageable) {
        return TryGetService.execute(()-> repository.findAllTurmasByCurso(curso, pageable), log);
    }
}
