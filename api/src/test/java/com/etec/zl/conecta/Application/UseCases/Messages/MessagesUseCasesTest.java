package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOInfoMessage;
import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.NotificationService;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Casos de Uso de Mensagens")
class MessagesUseCasesTest {

    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private MessageMapper messageMapper;

    @BeforeEach
    void setup() {
        messageRepository = mock(MessageRepository.class);
        userRepository = mock(UserRepository.class);
        messageMapper = mock(MessageMapper.class);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // EnviarMensagemUseCase
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("EnviarMensagemUseCase")
    class EnviarMensagemTests {

        private MessageRepository messageRepository;
        private UserRepository userRepository;
        private MessageMapper messageMapper;
        private NotificationService notificationService;
        private EnviarMensagemUseCase useCase;

        @BeforeEach
        void setUp() {
            messageRepository = mock(MessageRepository.class);
            userRepository = mock(UserRepository.class);
            messageMapper = mock(MessageMapper.class);
            notificationService = mock(NotificationService.class);
            useCase = new EnviarMensagemUseCase(messageRepository, userRepository, messageMapper, notificationService);
        }

        @Test
        @DisplayName("deve salvar mensagem e enviar notificação quando a validação de tipo permitir")
        void enviaMensagemESalvaComSucesso() {
            var dto = dtoPadrao("sender-1", "receiver-1");
            var sender = mock(User.class);
            var receiver = mock(User.class);
            var notificadores = List.of(mock(Notificador.class));
            var nomeSender = new Name("João Silva");
            var message = mock(Message.class);

            when(sender.getTipo()).thenReturn(Tipo.ALUNO);
            when(sender.getNome()).thenReturn(nomeSender);
            when(receiver.getTipo()).thenReturn(Tipo.PROFESSOR);
            when(userRepository.findById("sender-1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("receiver-1")).thenReturn(Optional.of(receiver));
            when(messageMapper.toRegister(dto)).thenReturn(message);
            when(userRepository.findNotificadoresByUserId("receiver-1")).thenReturn(notificadores);

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));

            verify(messageRepository, times(1)).save(message);
            verify(notificationService, times(1))
                    .sendNotifications(notificadores, "João Silva", "Olá, tudo bem?");
        }

        @Test
        @DisplayName("deve lançar ValidationFailedException e não enviar notificação quando ALUNO tenta enviar para ALUNO")
        void naoEnviaNotificacaoComValidacaoFalha() {
            var dto = dtoPadrao("aluno-1", "aluno-2");
            var aluno1 = mock(User.class);
            var aluno2 = mock(User.class);

            when(aluno1.getTipo()).thenReturn(Tipo.ALUNO);
            when(aluno1.getNome()).thenReturn(new Name("João Silva"));
            when(aluno2.getTipo()).thenReturn(Tipo.ALUNO);
            when(userRepository.findById("aluno-1")).thenReturn(Optional.of(aluno1));
            when(userRepository.findById("aluno-2")).thenReturn(Optional.of(aluno2));

            assertThrows(ValidationFailedException.class, () -> useCase.enviarMensagem(dto));

            verify(messageRepository, never()).save(any());
            verify(notificationService, never()).sendNotifications(any(), any(), any());
        }

        @Test
        @DisplayName("deve lançar UserNotFoundException e não enviar notificação quando sender não existe")
        void naoEnviaNotificacaoComSenderInexistente() {
            var dto = dtoPadrao("sender-x", "receiver-1");
            when(userRepository.findById("sender-x")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> useCase.enviarMensagem(dto));

            verify(messageRepository, never()).save(any());
            verify(notificationService, never()).sendNotifications(any(), any(), any());
        }

        @Test
        @DisplayName("não deve enviar notificação quando save lança exceção")
        void naoEnviaNotificacaoComFalhaNaSave() {
            var dto = dtoPadrao("sender-1", "receiver-1");
            var sender = mock(User.class);
            var receiver = mock(User.class);
            var message = mock(Message.class);

            when(sender.getTipo()).thenReturn(Tipo.SECRETARIA);
            when(sender.getNome()).thenReturn(new Name("Secretária"));
            when(receiver.getTipo()).thenReturn(Tipo.ALUNO);
            when(userRepository.findById("sender-1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("receiver-1")).thenReturn(Optional.of(receiver));
            when(messageMapper.toRegister(dto)).thenReturn(message);
            doThrow(new RuntimeException("falha no banco")).when(messageRepository).save(message);

            assertThrows(RuntimeException.class, () -> useCase.enviarMensagem(dto));

            verify(notificationService, never()).sendNotifications(any(), any(), any());
        }

        @Nested
        @DisplayName("LerMensagensUseCase")
        class LerMensagensTests {

            @Test
            @DisplayName("Deve retornar lista de mensagens mapeadas")
            void lerMensagensMapeadas() {
                var useCase = new LerMensagensUseCase(messageRepository, messageMapper, userRepository);
                var pageRequest = new PageRequest(0, 20);

                var pageResult = new PageResult<>(List.of(mock(com.etec.zl.conecta.Domain.Entities.Messages.Message.class)), 0, 20, 1L, 1);
                when(messageRepository.ListarMensagens(anyString(), anyString(), eq(pageRequest))).thenReturn(pageResult);

                User sender = mock(User.class);
                when(sender.getNome()).thenReturn(new Name("Leonardo teste da Silva"));
                when(userRepository.findById(any())).thenReturn(Optional.of(sender));

                var result = useCase.lerMensagens("sender", "receiver", pageRequest);

                assertNotNull(result);
                verify(messageMapper, atLeastOnce()).toReturn(any(), any());
            }
        }
    }
    private DTORegisterMessage dtoPadrao(java.lang.String senderId, java.lang.String receiverId) {
        return new DTORegisterMessage(
                senderId,
                receiverId,
                new DTOInfoMessage(
                        new String("Olá, tudo bem?"),
                        new Midia(TipoMidia.FOTO, "https://exemplo.com/foto.jpg")
                )
        );
    }
}