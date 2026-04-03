package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProfessorListagemPorTurmaPort {

    PageResult<DTORetornoNormal> professorListagemPorTurma(String id_professor, UUID id_turma, PageRequest pageable);
}
