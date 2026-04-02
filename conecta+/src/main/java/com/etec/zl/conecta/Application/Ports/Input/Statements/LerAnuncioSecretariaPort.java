package com.etec.zl.conecta.Application.Ports.Input.Statements;

import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LerAnuncioSecretariaPort {

    PageResult<Statement> lerAnunciosSecretaria(PageRequest pageable);
}
