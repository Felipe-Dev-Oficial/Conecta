package com.etec.zl.conecta.Application.Ports.Input.Solicitations;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTORequirement;

public interface SendSolicitationPort {

    void sendSolicitation(String userId, DTORequirement dto);
}
