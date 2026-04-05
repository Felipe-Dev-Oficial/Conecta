package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.Ports.Input.Turmas.PassaModuloPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassaModuloUseCase implements PassaModuloPort {

    private static final Logger log = LoggerFactory.getLogger(PassaModuloUseCase.class);

    private final TurmaRepository repository;

    public PassaModuloUseCase(TurmaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void passaModulo() {
        try {
            repository.passaModulo();
        } catch (Exception e) {
            var pe = new ProcessingErrorException(e.getMessage());
            log.error(pe.getMessage(), e);
            throw pe;
        }
    }
}
