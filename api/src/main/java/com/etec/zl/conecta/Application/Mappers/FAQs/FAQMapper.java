package com.etec.zl.conecta.Application.Mappers.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.Name;

public class FAQMapper {

    public FAQ toRegister(String id, DTORegisterFAQ dto) {
        return new FAQ(
                id,
                dto.question(),
                dto.answer(),
                dto.relevance()
        );
    }
    public DTOReturnFAQ toReturn(FAQ faq) {
        return new DTOReturnFAQ(
                faq.getQuestion(),
                faq.getAnswer()
        );
    }
}
