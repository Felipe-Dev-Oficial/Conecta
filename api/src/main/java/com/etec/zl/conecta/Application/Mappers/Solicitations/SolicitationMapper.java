package com.etec.zl.conecta.Application.Mappers.Solicitations;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTORequirement;
import com.etec.zl.conecta.Application.DTOs.Solicitations.DTOReturnRequirement;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.Entities.Users.User;

public class SolicitationMapper {

    public Solicitation toRegister(User user, DTORequirement dto) {
        return new Solicitation(
                dto.typeRequirement(),
                dto.otherRequirement(),
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getTurmasIds()
        );
    }
    public DTOReturnRequirement toDTOReturnRequirement(Solicitation solicitation) {
        return new DTOReturnRequirement(
                solicitation.getId(),
                solicitation.getTypeRequirement(),
                solicitation.getOtherRequirement(),
                solicitation.isSolved(),
                solicitation.getCreatedAt()
        );
    }
}
