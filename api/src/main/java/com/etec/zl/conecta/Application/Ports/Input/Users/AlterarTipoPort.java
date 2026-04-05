package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Domain.ValueObjects.Tipo;

public interface AlterarTipoPort {

    void alterarTipo(String id, Tipo tipo);
}
