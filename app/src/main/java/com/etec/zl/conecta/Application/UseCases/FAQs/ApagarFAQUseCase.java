package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.Ports.Input.FAQs.ApagarFAQPort;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ApagarFAQUseCase implements ApagarFAQPort {

    private static final Logger log = LoggerFactory.getLogger(ApagarFAQUseCase.class);

    private final VerifyIfExistsModifyAndSaveFAQsService service;


    public ApagarFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        this.service = service;
    }

    @Override
    public void apagarFAQ(UUID faqId) {
        service.execute(
                faqId,
                FAQ::apagarFAQ,
                log
        );
    }
}
