package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

import java.util.List;

public interface AlunoListagemProfessorPort {

    PageResult<DTORetornoNormal> alunoListagemProfessor(String id, PageRequest pageable);
}
