package com.etec.zl.conecta.Application.Ports.Output.Repositories;

import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface SolicitationRepository {

    void saveSolicitation(Solicitation solicitation);
    Optional<Solicitation> getSolicitationById(UUID id);
    PageResult<Solicitation> getSolicitationsByUser(String id, PageRequest pageRequest);
    PageResult<Solicitation> getSolicitationsBySearch(String search, PageRequest pageRequest);
}