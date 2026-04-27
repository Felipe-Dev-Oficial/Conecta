package com.etec.zl.conecta.Application.Ports.Input.Solicitations;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTOReturnRequirement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

public interface GetSelfSolicitationPort {

    PageResult<DTOReturnRequirement> getSolicitations(String userId, PageRequest pageRequest);
}
