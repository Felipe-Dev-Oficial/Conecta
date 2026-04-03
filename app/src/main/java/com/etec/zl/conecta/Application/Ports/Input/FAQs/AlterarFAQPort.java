package com.etec.zl.conecta.Application.Ports.Input.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTOUpdateFaq;

import java.util.UUID;

public interface AlterarFAQPort {

    void alterarFAQ(UUID faqId, DTOUpdateFaq dto);
}
