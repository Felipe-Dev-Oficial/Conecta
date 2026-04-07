package com.etec.zl.conecta.Application.Ports.Input.Turmas;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;

import java.util.UUID;

public interface EncontraTurmaPorIdPort {

    Turma findTurmaPorId(String id);
}
