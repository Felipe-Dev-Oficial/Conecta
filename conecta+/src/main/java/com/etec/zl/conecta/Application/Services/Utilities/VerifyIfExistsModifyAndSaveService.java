package com.etec.zl.conecta.Application.Services.Utilities;

import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VerifyIfExistsModifyAndSaveService {

    public static <T, X extends RuntimeException> void  execute(
            Supplier<Optional<T>> getMethod,
            Consumer<T> modifyMethod,
            Supplier<X> ex,
            Consumer<T> saveAction,
            Logger log
    ){

        T entity = TryGetByService.execute(getMethod, ex, log);
        modifyMethod.accept(entity);
        TrySaveService.execute(entity, saveAction, log);
    }
}
