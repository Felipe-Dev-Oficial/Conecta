package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Ports.Input.FAQs.LerFAQPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LerFAQUseCase implements LerFAQPort {

    private static final Logger log = LoggerFactory.getLogger(LerFAQUseCase.class);

    private final FAQRepository repository;
    private final FAQMapper mapper;

    public LerFAQUseCase(FAQRepository repository, FAQMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PageResult<DTOReturnFAQ> lerFAQs(PageRequest pageable) {
        return TryGetService.execute(
                ()-> repository.getAllActives(pageable),
                log
        )
                .map(mapper::toReturn);
    }
}
