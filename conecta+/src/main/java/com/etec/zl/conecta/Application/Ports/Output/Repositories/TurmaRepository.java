package com.etec.zl.conecta.Application.Ports.Output.Repositories;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import java.util.Optional;
import java.util.UUID;

public interface TurmaRepository {

    void save(Turma turma);
    void passaModulo();
    Optional<Turma> findById(UUID id);
    PageResult<Turma> findAllTurmas(PageRequest pageable);
    PageResult<Turma> findAllTurmasAtuais(PageRequest pageable);
    PageResult<Turma> findAllTurmasByCurso(Cursos curso, PageRequest pageable);
    PageResult<Turma> findAllTurmasByCursoAtuais(Cursos curso, PageRequest pageable);
}
