package com.etec.zl.conecta.Application.Ports.Input.Turmas;

import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;

import java.util.List;

public interface NovasTurmasPort {

    void cadastroTurmas(List<DTOCadastroTurma> cadastros);
}
