package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Password;

import java.util.UUID;

public interface AlterarSenhaPort {

    void alterarSenha(String id_user, UUID token, Password senha);
}
