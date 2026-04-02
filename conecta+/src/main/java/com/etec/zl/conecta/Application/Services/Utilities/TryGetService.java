package com.etec.zl.conecta.Application.Services.Utilities;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class TryGetService {

    public static <T> PageResult<T> execute(
            Supplier<PageResult<T>> getMethod,
            Logger log
    ){
        try {
            return getMethod.get();
        } catch (Exception e) {
            var pe = new ProcessingErrorException();
            log.error(pe.getMessage(), e);
            throw pe;
        }
    }
}
