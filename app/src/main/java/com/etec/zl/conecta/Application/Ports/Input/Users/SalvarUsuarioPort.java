package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;

public interface SalvarUsuarioPort {

    void salvarUsuario(DTOCadastro dto);
}
