package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.Ports.Input.FAQs.PublicarFAQPort;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PublicarFAQUseCase implements PublicarFAQPort {

    private static final Logger log = LoggerFactory.getLogger(PublicarFAQUseCase.class);

    private final VerifyIfExistsModifyAndSaveFAQsService service;

    public PublicarFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        this.service = service;
    }

    @Override
    public void publicarFAQ(UUID faqId) {
        service.execute(
                faqId,
                FAQ::publicar,
                log
        );
    }
}
