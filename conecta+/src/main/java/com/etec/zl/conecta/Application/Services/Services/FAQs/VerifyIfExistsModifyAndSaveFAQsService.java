package com.etec.zl.conecta.Application.Services.Services.FAQs;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Services.Utilities.VerifyIfExistsModifyAndSaveService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Consumer;

public class VerifyIfExistsModifyAndSaveFAQsService {

    private final FAQRepository repository;

    public VerifyIfExistsModifyAndSaveFAQsService(FAQRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID faqId, Consumer<FAQ> modifyMethod, Logger log){
        VerifyIfExistsModifyAndSaveService.execute(
                ()-> repository.getById(faqId),
                modifyMethod,
                ()-> new InvalidDataException("nenhum FAQ encontrado"),
                repository::save,
                log
        );
    }
}
