package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfessorListagemPorNomePort {

    PageResult<DTORetornoNormal> professorListagemPorNome(String id, Name name, PageRequest pageable);
}
