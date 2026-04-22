package com.etec.zl.conecta.Application.Ports.Input.Turmas;

import com.etec.zl.conecta.Domain.ValueObjects.Cursos;

import java.util.List;

public interface NovasTurmasPort {

    void cadastroTurmas(List<Cursos> cadastros);
}
