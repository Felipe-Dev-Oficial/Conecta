package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOContatos;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Input.Messages.RetornarContatosPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetornarContatosUseCase implements RetornarContatosPort {

    private static final Logger log = LoggerFactory.getLogger(RetornarContatosUseCase.class);

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final MessageMapper mapper;

    public RetornarContatosUseCase(MessageRepository repository, UserRepository userRepository, MessageMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public SliceResult<DTOContatos> retornarContatos(String id, PageRequest pageable) {
        TryGetByService.execute(() -> userRepository.findById(id), UserNotFoundException::new, log);
        try {
            SliceResult<String> contatosIds = repository.contatos(id, pageable);

            return contatosIds.map(contactId -> {
                if (contactId == null) return null;

                var contactUser = TryGetByService.execute(
                        () -> userRepository.findById(contactId),
                        UserNotFoundException::new,
                        log);

                return mapper.toReturnContatos(contactUser.getNome(), contactUser.getId());
            });

        } catch (RuntimeException e) {
            log.error("Erro ao processar contatos para o usuário {}: {}", id, e.getMessage());
            throw new ProcessingErrorException();
        }
    }
}
