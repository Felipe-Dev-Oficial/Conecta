package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessageSecretaria;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Input.Messages.LerMensagensSecretariaPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetService;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LerMensagensSecretariaUseCase implements LerMensagensSecretariaPort {

    private static final Logger log = LoggerFactory.getLogger(LerMensagensSecretariaUseCase.class);

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final MessageMapper mapper;

    public LerMensagensSecretariaUseCase(MessageRepository repository, UserRepository userRepository, MessageMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public PageResult<DTOReturnMessageSecretaria> lerMensagensSecretaria(String idSender, String idReceiver, PageRequest pageable) {
        var sender = TryGetByService.execute(()-> userRepository.findById(idSender), UserNotFoundException::new, log).getNome();
        var receiver = TryGetByService.execute(()-> userRepository.findById(idReceiver), UserNotFoundException::new, log).getNome();

        return TryGetService.execute(()-> repository.ListarMensagensSecretaria(idSender, idReceiver, pageable), log)
                .map(m -> mapper.toReturnSecretaria(sender, receiver, m));
    }
}
