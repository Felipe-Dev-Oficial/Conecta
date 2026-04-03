package com.etec.zl.conecta.Application.Ports.Input.FAQs;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

public interface LerFAQsSecretariaPort {

    PageResult<FAQ> lerFAQsSecretaria(PageRequest pageable);
}
