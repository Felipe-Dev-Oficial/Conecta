package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;

public interface AlunoBuscaProfessorPorIdPort {

    DTORetornoNormal alunoBuscaProfessorPorId(String id_aluno, String id_professor);
}
