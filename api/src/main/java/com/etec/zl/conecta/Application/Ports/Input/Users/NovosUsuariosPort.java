package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;

import java.util.List;

public interface NovosUsuariosPort {

    void novosUsuarios(List<DTOCadastro> usuariosNovos);
}
