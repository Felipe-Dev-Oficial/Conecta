package com.etec.zl.conecta.Application.Ports.Input.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;


public interface LerFAQPort {

    PageResult<DTOReturnFAQ> lerFAQs(PageRequest pageable);
}
