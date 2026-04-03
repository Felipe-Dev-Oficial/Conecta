package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAlteraAnuncio;
import com.etec.zl.conecta.Application.Ports.Input.Statements.AlterarAnuncioPort;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AlterarAnuncioUseCase implements AlterarAnuncioPort {

    private static final Logger log = LoggerFactory.getLogger(AlterarAnuncioUseCase.class);

    private final VerifyIfExistsModifyAndSaveStatementsService service;

    public AlterarAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {
        this.service = service;
    }

    @Override
    public void alterarAnuncio(UUID idAnuncio, DTOAlteraAnuncio dto) {
        service.execute(
                idAnuncio,
                u -> {
                    u.alterarMidia(dto.midia());
                    u.alterarTitulo(dto.title());
                    u.alterarPrioridade(dto.priority());
                    u.alterarConteudo(dto.content());
                },
                log
        );
    }
}
