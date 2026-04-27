package com.etec.zl.conecta.Application.UseCases.Solicitations;

import com.etec.zl.conecta.Application.Ports.Input.Solicitations.GetSolicitationsSecretariaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.SolicitationRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSolicitationsSecretariaUseCase implements GetSolicitationsSecretariaPort {

    private static final Logger logger = LoggerFactory.getLogger(GetSolicitationsSecretariaUseCase.class);

    private final SolicitationRepository repository;

    public GetSolicitationsSecretariaUseCase(SolicitationRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<Solicitation> getSolicitationsSecretaria(String search, PageRequest pageRequest) {
        return TryGetService.execute(() -> repository.getSolicitationsBySearch(search, pageRequest), logger);
    }
}
