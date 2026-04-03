package com.etec.zl.conecta.Application.UseCases.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.*;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.MessageRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Messages UseCases")
class MessagesUseCasesTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(String id, Tipo tipo) {
        return new User(id, new Name("João Silva"), new Email("joao@etec.com"),
                new PhoneNumber("11987654321"), new Password("Etec@1234"), tipo, new ArrayList<>());
    }

    private Message buildMessage(String senderId, String receiverId) {
        return new Message(UUID.randomUUID(), senderId, receiverId,
                Instant.now(), new Content("Olá!"), null);
    }

    private PageResult<Message> pageOf(Message... messages) {
        return new PageResult<>(List.of(messages), 0, 10, messages.length, 1);
    }

    // ─── EnviarMensagemUseCase ────────────────────────────────────────────────

    @Nested
    @DisplayName("EnviarMensagemUseCase")
    class EnviarMensagemTest {

        @Mock MessageRepository messageRepository;
        @Mock UserRepository userRepository;
        @Mock MessageMapper mapper;

        EnviarMensagemUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new EnviarMensagemUseCase(messageRepository, userRepository, mapper);
        }

        @Test
        @DisplayName("ALUNO pode enviar mensagem para SECRETARIA")
        void aluno_paraSecretaria_sucesso() {
            var sender = buildUser("s1", Tipo.ALUNO);
            var receiver = buildUser("r1", Tipo.SECRETARIA);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));
            var msg = buildMessage("s1", "r1");

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
            when(mapper.toRegister(dto)).thenReturn(msg);

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
            verify(messageRepository).save(msg);
        }

        @Test
        @DisplayName("ALUNO pode enviar mensagem para PROFESSOR")
        void aluno_paraProfessor_sucesso() {
            var sender = buildUser("s1", Tipo.ALUNO);
            var receiver = buildUser("r1", Tipo.PROFESSOR);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
            when(mapper.toRegister(dto)).thenReturn(buildMessage("s1", "r1"));

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
        }

        @Test
        @DisplayName("ALUNO NÃO pode enviar mensagem para outro ALUNO")
        void aluno_paraAluno_throwsValidation() {
            var sender = buildUser("s1", Tipo.ALUNO);
            var receiver = buildUser("r1", Tipo.ALUNO);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));

            assertThrows(ValidationFailedException.class, () -> useCase.enviarMensagem(dto));
            verify(messageRepository, never()).save(any());
        }

        @Test
        @DisplayName("PROFESSOR pode enviar mensagem para ALUNO")
        void professor_paraAluno_sucesso() {
            var sender = buildUser("s1", Tipo.PROFESSOR);
            var receiver = buildUser("r1", Tipo.ALUNO);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
            when(mapper.toRegister(dto)).thenReturn(buildMessage("s1", "r1"));

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
        }

        @Test
        @DisplayName("PROFESSOR pode enviar mensagem para outro PROFESSOR")
        void professor_paraProfessor_sucesso() {
            var sender = buildUser("s1", Tipo.PROFESSOR);
            var receiver = buildUser("r1", Tipo.PROFESSOR);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
            when(mapper.toRegister(dto)).thenReturn(buildMessage("s1", "r1"));

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
        }

        @Test
        @DisplayName("DESATIVADO pode enviar mensagem apenas para SECRETARIA")
        void desativado_paraSecretaria_sucesso() {
            var sender = buildUser("s1", Tipo.DESATIVADO);
            var receiver = buildUser("r1", Tipo.SECRETARIA);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
            when(mapper.toRegister(dto)).thenReturn(buildMessage("s1", "r1"));

            assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
        }

        @Test
        @DisplayName("DESATIVADO NÃO pode enviar mensagem para PROFESSOR")
        void desativado_paraProfessor_throwsValidation() {
            var sender = buildUser("s1", Tipo.DESATIVADO);
            var receiver = buildUser("r1", Tipo.PROFESSOR);
            var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));

            assertThrows(ValidationFailedException.class, () -> useCase.enviarMensagem(dto));
            verify(messageRepository, never()).save(any());
        }

        @Test
        @DisplayName("SECRETARIA pode enviar para qualquer tipo")
        void secretaria_paraTodos_sucesso() {
            for (Tipo tipo : List.of(Tipo.ALUNO, Tipo.PROFESSOR, Tipo.SECRETARIA)) {
                var sender = buildUser("s1", Tipo.SECRETARIA);
                var receiver = buildUser("r1", tipo);
                var dto = new DTORegisterMessage("s1", "r1", new DTOInfoMessage(new Content("Oi"), null));

                when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
                when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
                when(mapper.toRegister(dto)).thenReturn(buildMessage("s1", "r1"));

                assertDoesNotThrow(() -> useCase.enviarMensagem(dto));
            }
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se sender não existe")
        void sender_naoEncontrado_throwsUserNotFound() {
            var dto = new DTORegisterMessage("s-inexistente", "r1", new DTOInfoMessage(new Content("Oi"), null));
            when(userRepository.findById("s-inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> useCase.enviarMensagem(dto));
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se receiver não existe")
        void receiver_naoEncontrado_throwsUserNotFound() {
            var sender = buildUser("s1", Tipo.ALUNO);
            var dto = new DTORegisterMessage("s1", "r-inexistente", new DTOInfoMessage(new Content("Oi"), null));

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r-inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> useCase.enviarMensagem(dto));
        }
    }

    // ─── LerMensagensUseCase ──────────────────────────────────────────────────

    @Nested
    @DisplayName("LerMensagensUseCase")
    class LerMensagensTest {

        @Mock MessageRepository messageRepository;
        @Mock UserRepository userRepository;
        @Mock MessageMapper mapper;

        LerMensagensUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new LerMensagensUseCase(messageRepository, mapper, userRepository);
        }

        @Test
        @DisplayName("Deve retornar PageResult com mensagens mapeadas")
        void lerMensagens_sucesso() {
            var msg = buildMessage("s1", "r1");
            var sender = buildUser("s1", Tipo.ALUNO);
            var dtoReturn = new DTOReturnMessage(sender.getNome(), msg.getContent(), null);
            var page = pageOf(msg);
            var req = new PageRequest(0, 10);

            when(messageRepository.ListarMensagens("s1", "r1", req)).thenReturn(page);
            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(mapper.toReturn(sender.getNome(), msg)).thenReturn(dtoReturn);

            var result = useCase.lerMensagens("s1", "r1", req);

            assertEquals(1, result.content().size());
            assertEquals(dtoReturn, result.content().get(0));
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se sender da mensagem não existe")
        void lerMensagens_senderNaoEncontrado() {
            var msg = buildMessage("s-inexistente", "r1");
            var req = new PageRequest(0, 10);

            when(messageRepository.ListarMensagens("s-inexistente", "r1", req)).thenReturn(pageOf(msg));
            when(userRepository.findById("s-inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.lerMensagens("s-inexistente", "r1", req));
        }
    }

    // ─── LerMensagensSecretariaUseCase ────────────────────────────────────────

    @Nested
    @DisplayName("LerMensagensSecretariaUseCase")
    class LerMensagensSecretariaTest {

        @Mock MessageRepository messageRepository;
        @Mock UserRepository userRepository;
        @Mock MessageMapper mapper;

        LerMensagensSecretariaUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new LerMensagensSecretariaUseCase(messageRepository, userRepository, mapper);
        }

        @Test
        @DisplayName("Deve retornar PageResult com mensagens mapeadas para secretaria")
        void lerMensagensSecretaria_sucesso() {
            var sender = buildUser("s1", Tipo.SECRETARIA);
            var receiver = buildUser("r1", Tipo.ALUNO);
            var msg = buildMessage("s1", "r1");
            var dto = new DTOReturnMessageSecretaria(msg.getId(), sender.getNome(), "s1",
                    receiver.getNome(), "r1", msg.getTimestamp(), msg.getContent(), null);
            var req = new PageRequest(0, 10);

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r1")).thenReturn(Optional.of(receiver));
            when(messageRepository.ListarMensagensSecretaria("s1", "r1", req)).thenReturn(pageOf(msg));
            when(mapper.toReturnSecretaria(sender.getNome(), receiver.getNome(), msg)).thenReturn(dto);

            var result = useCase.lerMensagensSecretaria("s1", "r1", req);

            assertEquals(1, result.content().size());
            verify(mapper).toReturnSecretaria(sender.getNome(), receiver.getNome(), msg);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se sender não existe")
        void lerMensagensSecretaria_senderNaoEncontrado() {
            var req = new PageRequest(0, 10);
            when(userRepository.findById("s-inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.lerMensagensSecretaria("s-inexistente", "r1", req));
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se receiver não existe")
        void lerMensagensSecretaria_receiverNaoEncontrado() {
            var sender = buildUser("s1", Tipo.SECRETARIA);
            var req = new PageRequest(0, 10);

            when(userRepository.findById("s1")).thenReturn(Optional.of(sender));
            when(userRepository.findById("r-inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.lerMensagensSecretaria("s1", "r-inexistente", req));
        }
    }

    // ─── RetornarContatosUseCase ──────────────────────────────────────────────

    @Nested
    @DisplayName("RetornarContatosUseCase")
    class RetornarContatosTest {

        @Mock MessageRepository messageRepository;
        @Mock UserRepository userRepository;
        @Mock MessageMapper mapper;

        RetornarContatosUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new RetornarContatosUseCase(messageRepository, userRepository, mapper);
        }

        @Test
        @DisplayName("Deve retornar SliceResult de contatos")
        void retornarContatos_sucesso() {
            var user = buildUser("u1", Tipo.ALUNO);
            var contato = buildUser("c1", Tipo.PROFESSOR);
            var slice = new SliceResult<>(List.of("c1"), 0, 10, false);
            var req = new PageRequest(0, 10);
            var dtoContato = new DTOContatos(contato.getNome(), "c1");

            when(userRepository.findById("u1")).thenReturn(Optional.of(user));
            when(messageRepository.contatos("u1", req)).thenReturn(slice);
            when(userRepository.findById("c1")).thenReturn(Optional.of(contato));
            when(mapper.toReturnContatos(contato.getNome(), "c1")).thenReturn(dtoContato);

            var result = useCase.retornarContatos("u1", req);

            assertEquals(1, result.content().size());
            assertEquals(dtoContato, result.content().get(0));
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se usuário principal não existe")
        void retornarContatos_usuarioNaoEncontrado() {
            var req = new PageRequest(0, 10);
            when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.retornarContatos("inexistente", req));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException se contato não existe")
        void retornarContatos_contatoNaoEncontrado() {
            var user = buildUser("u1", Tipo.ALUNO);
            var slice = new SliceResult<>(List.of("c-inexistente"), 0, 10, false);
            var req = new PageRequest(0, 10);

            when(userRepository.findById("u1")).thenReturn(Optional.of(user));
            when(messageRepository.contatos("u1", req)).thenReturn(slice);
            when(userRepository.findById("c-inexistente")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> useCase.retornarContatos("u1", req));
        }
    }
}