package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessage;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Input.Messages.LerMensagensPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LerMensagensUseCase implements LerMensagensPort {

    private static final Logger log = LoggerFactory.getLogger(LerMensagensUseCase.class);

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final UserRepository userRepository;

    public LerMensagensUseCase(MessageRepository repository, MessageMapper mapper, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    @Override
    public PageResult<DTOReturnMessage> lerMensagens(String idSender, String idReceiver, PageRequest pageable) {
        return TryGetService.execute(()-> repository.ListarMensagens(idSender, idReceiver, pageable), log)
                .map(m -> {
                    var sender = TryGetByService.execute(()-> userRepository.findById(m.getIdSender()), UserNotFoundException::new, log).getNome();
                    return mapper.toReturn(sender, m);
                });
    }
}
