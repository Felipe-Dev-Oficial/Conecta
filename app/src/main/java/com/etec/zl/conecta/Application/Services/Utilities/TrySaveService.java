package com.etec.zl.conecta.Application.Services.Utilities;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class TrySaveService {

    public static <T> void execute(
            T entity,
            Consumer<T> saveAction,
            Logger log
    ){
        try {
            saveAction.accept(entity);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new ProcessingErrorException();
        }
    }
}
