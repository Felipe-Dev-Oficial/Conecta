package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.Ports.Input.Users.DeletarNotificadorPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteNotificadorUseCase implements DeletarNotificadorPort {

    private static final Logger log = LoggerFactory.getLogger(DeleteNotificadorUseCase.class);

    private final UserRepository repository;

    public DeleteNotificadorUseCase(UserRepository repository) {
        this.repository = repository;
    }


    @Override
    public void deleteNotificador(String userId, String endpoint) {
        try {
            repository.deleteNotificador(userId, endpoint);
        } catch (UserNotFoundException ex) {
            log.warn(ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            var pe = new ProcessingErrorException();
            log.error(pe.getMessage(), ex);
            throw pe;
        }
    }
}
