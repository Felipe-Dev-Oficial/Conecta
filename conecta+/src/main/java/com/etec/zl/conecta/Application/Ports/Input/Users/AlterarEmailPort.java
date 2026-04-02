package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Domain.ValueObjects.Email;

import java.util.UUID;

public interface AlterarEmailPort {

    void alterarEmail(String id, UUID token, Email email);
}
