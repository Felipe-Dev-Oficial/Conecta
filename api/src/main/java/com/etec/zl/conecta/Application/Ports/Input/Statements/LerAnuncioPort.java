package com.etec.zl.conecta.Application.Ports.Input.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LerAnuncioPort {

    PageResult<DTORetornoAnuncio> lerAnuncios(String reader_id, PageRequest pageable);
}
