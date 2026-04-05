package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Input.Messages.EnviarMensagemPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Utilities.TryGetByService;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnviarMensagemUseCase implements EnviarMensagemPort {

    private static final Logger log = LoggerFactory.getLogger(EnviarMensagemUseCase.class);

    private final MessageRepository repository;
    private final UserRepository userRepository;
    private final MessageMapper mapper;

    public EnviarMensagemUseCase(MessageRepository repository, UserRepository userRepository, MessageMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void enviarMensagem(DTORegisterMessage dto) {
        var sender = TryGetByService.execute(()-> userRepository.findById(dto.idSender()),()-> new UserNotFoundException(), log);
        var receiver = TryGetByService.execute(()-> userRepository.findById(dto.idReceiver()),()-> new UserNotFoundException(), log);
        var tipoSender = sender.getTipo();
        var tipoReceiver = receiver.getTipo();
        if (!validaMensagem(tipoSender, tipoReceiver)) {
            var e = new ValidationFailedException("Não se pode enviar mensagem para " + tipoReceiver + " sendo " + tipoSender);
            log.warn("name sender: " + sender.getNome().name() + " " + e.getMessage(), e);
            throw e;
        }
        TrySaveService.execute(mapper.toRegister(dto), repository::save, log);
    }

    private boolean validaMensagem(Tipo sender, Tipo receiver) {
        return switch (sender) {
            case ALUNO -> switch (receiver) {
                case SECRETARIA, PROFESSOR -> true;
                default -> false;
            };
            case PROFESSOR -> switch (receiver) {
                case PROFESSOR, SECRETARIA, ALUNO -> true;
                default -> false;
            };
            case DESATIVADO -> switch (receiver) {
                case SECRETARIA -> true;
                default -> false;
            };
            case SECRETARIA -> true;
        };
    }
}
