package com.etec.zl.conecta.Application.Ports.Output.Repositories;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface FAQRepository {

    void save(FAQ faq);
    PageResult<FAQ> getAllActives(PageRequest pageable);
    PageResult<FAQ> getAll(PageRequest pageable);
    Optional<FAQ> getById(UUID idFaq);
}
