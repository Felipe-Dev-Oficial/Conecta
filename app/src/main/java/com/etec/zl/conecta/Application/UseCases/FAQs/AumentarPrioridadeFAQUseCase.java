package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.Ports.Input.FAQs.AumentarPrioridadeFAQPort;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AumentarPrioridadeFAQUseCase implements AumentarPrioridadeFAQPort {

    private static final Logger log = LoggerFactory.getLogger(AumentarPrioridadeFAQUseCase.class);

    private final VerifyIfExistsModifyAndSaveFAQsService service;

    public AumentarPrioridadeFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        this.service = service;
    }

    @Override
    public void elevarPrioridadeFAQ(UUID faq_id) {
        service.execute(
                faq_id,
                FAQ::elevarPrioridade,
                log
        );
    }
}
