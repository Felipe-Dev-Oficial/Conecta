package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.Ports.Input.FAQs.DiminuirPrioridadeFAQPort;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class DiminuirPrioridadeFAQUseCase implements DiminuirPrioridadeFAQPort {

    private static final Logger log = LoggerFactory.getLogger(DiminuirPrioridadeFAQUseCase.class);

    private final VerifyIfExistsModifyAndSaveFAQsService service;

    public DiminuirPrioridadeFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        this.service = service;
    }

    @Override
    public void diminuirPrioridadeFAQ(UUID faq_id) {
        service.execute(
                faq_id,
                FAQ::reduzirPrioridade,
                log
        );
    }
}
