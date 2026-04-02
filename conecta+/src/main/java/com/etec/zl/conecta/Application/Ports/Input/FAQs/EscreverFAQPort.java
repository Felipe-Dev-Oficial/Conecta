package com.etec.zl.conecta.Application.Ports.Input.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;

public interface EscreverFAQPort {

    void escreverFAQ(String id, DTORegisterFAQ dto);
}
