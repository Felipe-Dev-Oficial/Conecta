package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;
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

    @Nested
    @DisplayName("EnviarMensagemUseCase")
    class EnviarMensagemTests {

        @Test
        @DisplayName("Deve enviar mensagem com sucesso quando a validação de tipo permitir")
        void enviaMensagemSucesso() {
            var useCase = new EnviarMensagemUseCase(messageRepository, userRepository, messageMapper);
            var dto = new DTORegisterMessage("sender-1", "receiver-1", null);

            User sender = mock(User.class);
            User receiver = mock(User.class);

            when(sender.getTipo()).thenReturn(Tipo.ALUNO);
            when(sender.getNome()).thenReturn(new Name("Leonardo teste da Silva"));
            when(receiver.getTipo()).thenReturn(Tipo.PROFESSOR);

            when(userRepository.findById("sender-1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("receiver-1")).thenReturn(Optional.of(receiver));

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
            verify(messageRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("Deve lançar ValidationFailedException quando ALUNO tentar enviar para outro ALUNO")
        void validaRegraDeNegocio() {
            var useCase = new EnviarMensagemUseCase(messageRepository, userRepository, messageMapper);
            var dto = new DTORegisterMessage("aluno-1", "aluno-2", null);

            User aluno1 = mock(User.class);
            User aluno2 = mock(User.class);

            when(aluno1.getTipo()).thenReturn(Tipo.ALUNO);
            when(aluno1.getNome()).thenReturn(new Name("Leonardo teste da Silva"));
            when(aluno2.getTipo()).thenReturn(Tipo.ALUNO);

            when(userRepository.findById("aluno-1")).thenReturn(Optional.of(aluno1));
            when(userRepository.findById("aluno-2")).thenReturn(Optional.of(aluno2));

            assertThrows(ValidationFailedException.class, () -> useCase.enviarMensagem(dto));
            verify(messageRepository, never()).save(any());
        }
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