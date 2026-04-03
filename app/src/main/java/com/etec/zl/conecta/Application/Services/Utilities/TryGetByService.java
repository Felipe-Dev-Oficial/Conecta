package com.etec.zl.conecta.Application.Services.Utilities;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class TryGetByService {

    public static <T, X extends RuntimeException> T execute(
            Supplier<Optional<T>> findAction,
            Supplier<X> exceptionSupplier,
            Logger logger
    ){
        try {
            return findAction.get()
                    .orElseThrow(() -> {
                    X e = exceptionSupplier.get();
                    logger.warn(e.getMessage(), e);
                    return e;
                    });
        } catch (RuntimeException e){
            if (e.getClass().equals(exceptionSupplier.get().getClass())) {
                throw e;
            }

            var pe = new ProcessingErrorException();
            logger.error("Infrastructure failure: {}", e.getMessage(), e);
            throw pe;
        }
    }
}
