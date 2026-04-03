package com.etec.zl.conecta.Application.Ports.Input.FAQs;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;

import java.util.UUID;

public interface PublicarFAQPort {

    void publicarFAQ(UUID faqId);
}
