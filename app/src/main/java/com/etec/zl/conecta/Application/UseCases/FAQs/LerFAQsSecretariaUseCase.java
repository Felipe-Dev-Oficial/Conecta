package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Ports.Input.FAQs.LerFAQsSecretariaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LerFAQsSecretariaUseCase implements LerFAQsSecretariaPort {

    private static final Logger log = LoggerFactory.getLogger(LerFAQsSecretariaUseCase.class);

    private final FAQRepository repository;

    public LerFAQsSecretariaUseCase(FAQRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<FAQ> lerFAQsSecretaria(PageRequest pageable) {
        return TryGetService.execute(
                ()-> repository.getAll(pageable),
                log
        );
    }
}
