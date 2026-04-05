package com.etec.zl.conecta.Application.Ports.Input.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;

public interface GerarAnuncioPort {

    void gerarAnuncio(String id, DTOAnuncio dto);
}
