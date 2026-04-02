package com.etec.zl.conecta.Application.Ports.Input.Turmas;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarTodasAsTurmasAtuaisPort {

    PageResult<Turma> findAllTurmasAtuais(PageRequest pageable);
}
