package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;

public interface SecretariaBuscaPorIdPort {

    DTORetornoSecretaria secretariaBuscaPorId(String id);
}
