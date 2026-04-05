package com.etec.zl.conecta.Application.Ports.Input.Turmas;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarTurmasPorCursoPort {

    PageResult<Turma> findAllTurmasByCurso(Cursos curso, PageRequest pageable);
}
