package com.etec.zl.conecta.Application.Ports.Input.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

public interface LerAnunciosGeraisPort {

    PageResult<DTORetornoAnuncio> lerAnuncios(PageRequest pageable);
}
