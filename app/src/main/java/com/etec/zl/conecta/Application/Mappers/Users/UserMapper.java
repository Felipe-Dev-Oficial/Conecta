package com.etec.zl.conecta.Application.Mappers.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Domain.Entities.Users.User;

public class UserMapper {

    public DTORetornoNormal toDTOReturn(User user){
        return new DTORetornoNormal(
                user.getId(),
                user.getNome(),
                user.getTipo()
        );
    }
    public DTORetornoSecretaria toDTOReturnSecretaria(User user){
        return new DTORetornoSecretaria(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getNumero(),
                user.getTipo()
        );
    }
    public User toRegister (DTOCadastro dto){
        return new User(
                dto.id(),
                dto.nome(),
                dto.email(),
                dto.numero(),
                dto.senha(),
                dto.tipo(),
                dto.turmas()
        );
    }
}
