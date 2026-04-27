package com.etec.zl.conecta.Application.UseCases.Solicitations;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTOReturnRequirement;
import com.etec.zl.conecta.Application.Mappers.Solicitations.SolicitationMapper;
import com.etec.zl.conecta.Application.Ports.Input.Solicitations.GetSelfSolicitationPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.SolicitationRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSelfSolicitationUseCase implements GetSelfSolicitationPort {

    private static final Logger logger = LoggerFactory.getLogger(SendSolicitationUseCase.class);

    private final SolicitationRepository repository;
    private final SolicitationMapper mapper;

    public GetSelfSolicitationUseCase(SolicitationRepository repository, SolicitationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PageResult<DTOReturnRequirement> getSolicitations(String userId, PageRequest pageRequest) {
        return TryGetService.execute(() -> repository.getSolicitationsByUser(userId, pageRequest), logger)
                .map(mapper::toDTOReturnRequirement);
    }
}
