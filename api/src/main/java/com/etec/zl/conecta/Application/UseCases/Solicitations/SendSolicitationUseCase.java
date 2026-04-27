package com.etec.zl.conecta.Application.UseCases.Solicitations;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTORequirement;
import com.etec.zl.conecta.Application.Mappers.Solicitations.SolicitationMapper;
import com.etec.zl.conecta.Application.Ports.Input.Solicitations.SendSolicitationPort;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.SolicitationRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.NotificationService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Utilities.TrySaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendSolicitationUseCase implements SendSolicitationPort {

    private static final Logger log = LoggerFactory.getLogger(SendSolicitationUseCase.class);

    private final SolicitationRepository repository;
    private final UserRepository userRepository;
    private final TryGetByUserService userService;
    private final SolicitationMapper mapper;
    private final NotificationService notificationService;


    public SendSolicitationUseCase(SolicitationRepository repository, UserRepository userRepository, TryGetByUserService userService, SolicitationMapper mapper, NotificationService notificationService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mapper = mapper;
        this.notificationService = notificationService;
    }

    @Override
    public void sendSolicitation(String userId, DTORequirement dto) {
        TrySaveService.execute(
                mapper.toRegister(userService.execute(()-> userRepository.findById(userId), log), dto),
                repository::saveSolicitation,
                log
        );
        notificationService.sendNotifications(
                userRepository.findAllNotificadoresSecretariaSolicitation(),
                "Nova solicitação recebida",
                "Uma nova solicitação foi enviada pelo usuário " + userId + ". Verifique os detalhes para mais informações."
        );
    }
}
