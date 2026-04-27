package com.etec.zl.conecta.Application.UseCases.Solicitations;

import com.etec.zl.conecta.Application.Ports.Input.Solicitations.SolveSolicitationPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.SolicitationRepository;
import com.etec.zl.conecta.Application.Services.Utilities.VerifyIfExistsModifyAndSaveService;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SolveSolicitationUseCase implements SolveSolicitationPort {

    private static final Logger logger = LoggerFactory.getLogger(SolveSolicitationUseCase.class);

    private final SolicitationRepository repository;

    public SolveSolicitationUseCase(SolicitationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void solveSolicitation(UUID solicitationId) {
        VerifyIfExistsModifyAndSaveService.execute(
                ()-> repository.getSolicitationById(solicitationId),
                Solicitation::solveRequirement,
                ()-> new InvalidDataException("Solicitação não encontrada ou já resolvida."),
                repository::saveSolicitation,
                logger
        );
    }
}
