package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Ports.Input.Turmas.NovasTurmasPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NovasTurmasUseCase implements NovasTurmasPort {

    private static final Logger log = LoggerFactory.getLogger(NovasTurmasUseCase.class);

    private final TurmaRepository repository;
    private final TurmaMapper mapper;

    public NovasTurmasUseCase(TurmaRepository repository, TurmaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void cadastroTurmas(List<Cursos> cadastros) {
        cadastros.forEach(t -> {
            TrySaveService.execute(mapper.toRegister(t), repository::save, log);
        });
    }
}
