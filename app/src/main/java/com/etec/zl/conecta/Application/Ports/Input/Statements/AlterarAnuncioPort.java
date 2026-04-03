package com.etec.zl.conecta.Application.Ports.Input.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAlteraAnuncio;

import java.util.UUID;

public interface AlterarAnuncioPort {

    void alterarAnuncio(UUID idAnuncio, DTOAlteraAnuncio dto);
}
