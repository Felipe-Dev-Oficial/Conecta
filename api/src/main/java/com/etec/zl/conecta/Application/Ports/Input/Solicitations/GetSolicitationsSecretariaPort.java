package com.etec.zl.conecta.Application.Ports.Input.Solicitations;

import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

public interface GetSolicitationsSecretariaPort {

    PageResult<Solicitation> getSolicitationsSecretaria(String search, PageRequest pageRequest);
}
