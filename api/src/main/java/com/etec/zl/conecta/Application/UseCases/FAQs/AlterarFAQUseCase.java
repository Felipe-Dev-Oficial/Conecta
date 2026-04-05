package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTOUpdateFaq;
import com.etec.zl.conecta.Application.Ports.Input.FAQs.AlterarFAQPort;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AlterarFAQUseCase implements AlterarFAQPort {

    private static final Logger log = LoggerFactory.getLogger(AlterarFAQUseCase.class);

    private final VerifyIfExistsModifyAndSaveFAQsService service;

    public AlterarFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        this.service = service;
    }

    @Override
    public void alterarFAQ(UUID faqId, DTOUpdateFaq dto) {
        service.execute(
                faqId,
                f -> {
                    f.alterarPergunta(dto.pergunta());
                    f.alterarResposta(dto.resposta());
                },
                log
        );
    }
}
