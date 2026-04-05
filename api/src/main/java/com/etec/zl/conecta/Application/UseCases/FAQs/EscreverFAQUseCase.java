package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Ports.Input.FAQs.EscreverFAQPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EscreverFAQUseCase implements EscreverFAQPort {

    private static final Logger log = LoggerFactory.getLogger(EscreverFAQUseCase.class);

    private final FAQRepository repository;
    private final UserRepository userRepository;
    private final FAQMapper mapper;

    public EscreverFAQUseCase(FAQRepository repository, UserRepository userRepository, FAQMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void escreverFAQ(String id, DTORegisterFAQ dto) {
        var user = TryGetByService.execute(()-> userRepository.findById(id), UserNotFoundException::new, log).getId();
        TrySaveService.execute(mapper.toRegister(user, dto), repository::save, log);
    }
}
