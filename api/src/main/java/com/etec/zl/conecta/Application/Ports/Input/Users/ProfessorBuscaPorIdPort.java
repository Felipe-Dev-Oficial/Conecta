package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;

public interface ProfessorBuscaPorIdPort {

    DTORetornoNormal professorBuscaPorId(String id_professor, String id_aluno);
}
