package com.etec.zl.conecta.Application.Services.Services;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.EmailService;
import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Application.Services.Services.Users.StartChangeService;
import com.etec.zl.conecta.Application.Services.Services.Users.TrySaveUserService;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Application Services")
class ApplicationServiceTest {

    // ══════════════════════════════════════════════════════════════════════════
    // VerifyIfExistsModifyAndSaveFAQsService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveFAQsService")
    class FAQsServiceTests {

        private FAQRepository repository;
        private Logger log;
        private VerifyIfExistsModifyAndSaveFAQsService service;
        private UUID id;
        private FAQ faq;

        @BeforeEach
        void setUp() {
            repository = mock(FAQRepository.class);
            log        = mock(Logger.class);
            service    = new VerifyIfExistsModifyAndSaveFAQsService(repository);
            id         = UUID.randomUUID();
            faq        = new FAQ(id, "Pergunta?", "Resposta.", "autor",
                    StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.MEDIA);
        }

        @Nested
        @DisplayName("quando FAQ existe")
        class Sucesso {

            @Test
            @DisplayName("deve invocar modifyMethod com o FAQ encontrado")
            void invocaModify() {
                when(repository.getById(id)).thenReturn(Optional.of(faq));
                Consumer<FAQ> modify = mock();

                service.execute(id, modify, log);

                verify(modify, times(1)).accept(faq);
            }

            @Test
            @DisplayName("deve salvar o FAQ após o modify, nessa ordem")
            void salvaAposModify() {
                when(repository.getById(id)).thenReturn(Optional.of(faq));
                Consumer<FAQ> modify = mock();
                var order = inOrder(modify, repository);

                service.execute(id, modify, log);

                order.verify(modify).accept(faq);
                order.verify(repository).save(faq);
            }
        }

        @Nested
        @DisplayName("quando FAQ não existe")
        class NaoEncontrado {

            @Test
            @DisplayName("deve lançar InvalidDataException")
            void lancaInvalidData() {
                when(repository.getById(id)).thenReturn(Optional.empty());
                assertThrows(InvalidDataException.class,
                        () -> service.execute(id, f -> {}, log));
            }

            @Test
            @DisplayName("não deve salvar nada quando FAQ não existe")
            void naoSalva() {
                when(repository.getById(id)).thenReturn(Optional.empty());
                try { service.execute(id, f -> {}, log); } catch (InvalidDataException ignored) {}
                verify(repository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("quando save falha")
        class FalhaSave {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                when(repository.getById(id)).thenReturn(Optional.of(faq));
                doThrow(new RuntimeException("DB down")).when(repository).save(any());

                assertThrows(ProcessingErrorException.class,
                        () -> service.execute(id, f -> {}, log));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VerifyIfExistsModifyAndSaveStatementsService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveStatementsService")
    class StatementsServiceTests {

        private StatementRepository repository;
        private Logger log;
        private VerifyIfExistsModifyAndSaveStatementsService service;
        private UUID id;
        private Statement statement;

        @BeforeEach
        void setUp() {
            repository = mock(StatementRepository.class);
            log        = mock(Logger.class);
            service    = new VerifyIfExistsModifyAndSaveStatementsService(repository);
            id         = UUID.randomUUID();
            statement  = new Statement(
                    id, "sender",
                    new Content("Título"), Instant.now(),
                    new Content("Conteúdo"), null,
                    Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos()
            );
        }

        @Nested
        @DisplayName("quando statement existe")
        class Sucesso {

            @Test
            @DisplayName("deve invocar modifyMethod com o statement encontrado")
            void invocaModify() {
                when(repository.findById(id)).thenReturn(Optional.of(statement));
                Consumer<Statement> modify = mock();

                service.execute(id, modify, log);

                verify(modify, times(1)).accept(statement);
            }

            @Test
            @DisplayName("deve salvar o statement após o modify, nessa ordem")
            void salvaAposModify() {
                when(repository.findById(id)).thenReturn(Optional.of(statement));
                Consumer<Statement> modify = mock();
                var order = inOrder(modify, repository);

                service.execute(id, modify, log);

                order.verify(modify).accept(statement);
                order.verify(repository).save(statement);
            }
        }

        @Nested
        @DisplayName("quando statement não existe")
        class NaoEncontrado {

            @Test
            @DisplayName("deve lançar InvalidDataException")
            void lancaInvalidData() {
                when(repository.findById(id)).thenReturn(Optional.empty());
                assertThrows(InvalidDataException.class,
                        () -> service.execute(id, s -> {}, log));
            }

            @Test
            @DisplayName("não deve salvar nada quando statement não existe")
            void naoSalva() {
                when(repository.findById(id)).thenReturn(Optional.empty());
                try { service.execute(id, s -> {}, log); } catch (InvalidDataException ignored) {}
                verify(repository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("quando save falha")
        class FalhaSave {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                when(repository.findById(id)).thenReturn(Optional.of(statement));
                doThrow(new RuntimeException("DB down")).when(repository).save(any());

                assertThrows(ProcessingErrorException.class,
                        () -> service.execute(id, s -> {}, log));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // StartChangeService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("StartChangeService")
    class StartChangeServiceTests {

        private UserRepository repository;
        private EmailService emailService;
        private Logger log;
        private StartChangeService service;
        private User userPadrao;

        @BeforeEach
        void setUp() {
            repository   = mock(UserRepository.class);
            emailService = mock(EmailService.class);
            log          = mock(Logger.class);
            service      = new StartChangeService(repository, emailService);
            userPadrao   = new User(
                    "user-1",
                    new Name("João Silva"),
                    new Email("joao@etec.com"),
                    new PhoneNumber("11987654321"),
                    new Password("Etec@1234"),
                    Tipo.ALUNO,
                    new ArrayList<>()
            );
        }

        @Nested
        @DisplayName("fluxo feliz")
        class Sucesso {

            @Test
            @DisplayName("deve invocar action, emailService.send e repository.save exatamente uma vez")
            void invocaColaboradores() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                Consumer<User> action = mock();
                var updater = TokenUpdater.Start();
                Function<User, TokenUpdater> tokenExtractor = u -> updater;

                service.execute("user-1", action, tokenExtractor, "Assunto", log);

                verify(action,       times(1)).accept(userPadrao);
                verify(emailService, times(1)).send(anyString(), any(Email.class), eq("Assunto"));
                verify(repository,   times(1)).save(userPadrao);
            }

            @Test
            @DisplayName("o email enviado deve conter o token do updater")
            void emailContemToken() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                var updater = TokenUpdater.Start();

                service.execute("user-1", u -> {}, u -> updater, "Assunto", log);

                verify(emailService).send(contains(updater.token()), any(Email.class), anyString());
            }
        }

        @Nested
        @DisplayName("quando usuário não existe")
        class NaoEncontrado {

            @Test
            @DisplayName("deve lançar UserNotFoundException sem chamar emailService nem save")
            void lancaUserNotFound() {
                when(repository.findById("x")).thenReturn(Optional.empty());

                assertThrows(UserNotFoundException.class, () ->
                        service.execute("x", u -> {}, u -> TokenUpdater.Start(), "Assunto", log));

                verifyNoInteractions(emailService);
                verify(repository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("quando tokenExtractor retorna nulo")
        class TokenNulo {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void tokenNulo_processaError() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));

                assertThrows(ProcessingErrorException.class, () ->
                        service.execute("user-1", u -> {}, u -> null, "Assunto", log));
            }

            @Test
            @DisplayName("não deve salvar o usuário quando token é nulo")
            void naoSalvaComTokenNulo() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));

                try { service.execute("user-1", u -> {}, u -> null, "Assunto", log); }
                catch (ProcessingErrorException ignored) {}

                verify(repository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("quando emailService lança exceção")
        class FalhaEmail {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                doThrow(new RuntimeException("SMTP down")).when(emailService).send(anyString(), any(), anyString());
                var updater = TokenUpdater.Start();

                assertThrows(ProcessingErrorException.class, () ->
                        service.execute("user-1", u -> {}, u -> updater, "Assunto", log));
            }

            @Test
            @DisplayName("não deve salvar o usuário quando emailService falha")
            void naoSalvaComFalhaEmail() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                doThrow(new RuntimeException("SMTP down")).when(emailService).send(anyString(), any(), anyString());
                var updater = TokenUpdater.Start();

                try { service.execute("user-1", u -> {}, u -> updater, "Assunto", log); }
                catch (ProcessingErrorException ignored) {}

                verify(repository, never()).save(any());
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TrySaveUserService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("TrySaveUserService")
    class TrySaveUserServiceTests {

        private UserRepository userRepository;
        private TurmaRepository turmaRepository;
        private UserMapper mapper;
        private Logger log;
        private TrySaveUserService service;

        @BeforeEach
        void setUp() {
            userRepository  = mock(UserRepository.class);
            turmaRepository = mock(TurmaRepository.class);
            mapper          = mock(UserMapper.class);
            log             = mock(Logger.class);
            service         = new TrySaveUserService(userRepository, mapper, turmaRepository);
        }

        private DTOCadastro dtoCom(Tipo tipo, List<String> turmas) {
            return new DTOCadastro(
                    "user-1", new Name("João Silva"), new Email("joao@etec.com"),
                    new PhoneNumber("11987654321"), new Password("Etec@1234"), tipo, turmas
            );
        }

        private User userPadrao() {
            return new User("user-1", new Name("João Silva"), new Email("joao@etec.com"),
                    new PhoneNumber("11987654321"), new Password("Etec@1234"), Tipo.ALUNO, List.of());
        }

        @Nested
        @DisplayName("quando tipo é SECRETARIA")
        class Secretaria {

            @Test
            @DisplayName("deve salvar sem consultar turmaRepository")
            void salvaSemConsultarTurma() {
                var dto  = dtoCom(Tipo.SECRETARIA, List.of());
                var user = userPadrao();
                when(mapper.toRegister(dto)).thenReturn(user);

                service.execute(dto, log);

                verifyNoInteractions(turmaRepository);
                verify(userRepository, times(1)).save(user);
            }
        }

        @Nested
        @DisplayName("quando tipo é ALUNO ou PROFESSOR")
        class AlunoOuProfessor {

            @Test
            @DisplayName("deve consultar turmaRepository para cada turma da lista")
            void consultaTurmasInformadas() {
                var dto  = dtoCom(Tipo.ALUNO, List.of("t1", "t2"));
                var user = userPadrao();
                when(mapper.toRegister(dto)).thenReturn(user);
                when(turmaRepository.findById("t1")).thenReturn(Optional.of(mock(Turma.class)));
                when(turmaRepository.findById("t2")).thenReturn(Optional.of(mock(Turma.class)));

                service.execute(dto, log);

                verify(turmaRepository, times(1)).findById("t1");
                verify(turmaRepository, times(1)).findById("t2");
            }

            @Test
            @DisplayName("deve salvar o usuário quando todas as turmas existem")
            void salvaQuandoTurmasExistem() {
                var dto  = dtoCom(Tipo.PROFESSOR, List.of("t1"));
                var user = userPadrao();
                when(mapper.toRegister(dto)).thenReturn(user);
                when(turmaRepository.findById("t1")).thenReturn(Optional.of(mock(Turma.class)));

                service.execute(dto, log);

                verify(userRepository, times(1)).save(user);
            }

            @Test
            @DisplayName("deve lançar InvalidDataException quando turma não existe")
            void lancaExcecaoTurmaInexistente() {
                var dto = dtoCom(Tipo.ALUNO, List.of("turma-inexistente"));
                when(turmaRepository.findById("turma-inexistente")).thenReturn(Optional.empty());

                assertThrows(InvalidDataException.class, () -> service.execute(dto, log));
            }

            @Test
            @DisplayName("não deve salvar o usuário quando uma turma não existe")
            void naoSalvaComTurmaInexistente() {
                var dto = dtoCom(Tipo.ALUNO, List.of("turma-inexistente"));
                when(turmaRepository.findById("turma-inexistente")).thenReturn(Optional.empty());

                try { service.execute(dto, log); } catch (InvalidDataException ignored) {}

                verify(userRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("quando userRepository.save falha")
        class FalhaSave {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                var dto  = dtoCom(Tipo.SECRETARIA, List.of());
                var user = userPadrao();
                when(mapper.toRegister(dto)).thenReturn(user);
                doThrow(new RuntimeException("DB down")).when(userRepository).save(any());

                assertThrows(ProcessingErrorException.class, () -> service.execute(dto, log));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VerifyIfExistsModifyAndSaveUsersService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveUsersService")
    class UsersServiceTests {

        private UserRepository repository;
        private Logger log;
        private VerifyIfExistsModifyAndSaveUsersService service;
        private User userPadrao;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            log        = mock(Logger.class);
            service    = new VerifyIfExistsModifyAndSaveUsersService(repository);
            userPadrao = new User(
                    "user-1", new Name("João Silva"), new Email("joao@etec.com"),
                    new PhoneNumber("11987654321"), new Password("Etec@1234"),
                    Tipo.ALUNO, new ArrayList<>()
            );
        }

        @Nested
        @DisplayName("quando usuário existe")
        class Sucesso {

            @Test
            @DisplayName("deve invocar modifyMethod com o usuário encontrado")
            void invocaModify() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                Consumer<User> modify = mock();

                service.execute("user-1", log, modify);

                verify(modify, times(1)).accept(userPadrao);
            }

            @Test
            @DisplayName("deve salvar o usuário após o modify, nessa ordem")
            void salvaAposModify() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                Consumer<User> modify = mock();
                var order = inOrder(modify, repository);

                service.execute("user-1", log, modify);

                order.verify(modify).accept(userPadrao);
                order.verify(repository).save(userPadrao);
            }
        }

        @Nested
        @DisplayName("quando usuário não existe")
        class NaoEncontrado {

            @Test
            @DisplayName("deve lançar UserNotFoundException")
            void lancaUserNotFound() {
                when(repository.findById("x")).thenReturn(Optional.empty());
                assertThrows(UserNotFoundException.class,
                        () -> service.execute("x", log, u -> {}));
            }

            @Test
            @DisplayName("não deve salvar nada quando usuário não existe")
            void naoSalva() {
                when(repository.findById("x")).thenReturn(Optional.empty());
                try { service.execute("x", log, u -> {}); } catch (UserNotFoundException ignored) {}
                verify(repository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("quando save falha")
        class FalhaSave {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                when(repository.findById("user-1")).thenReturn(Optional.of(userPadrao));
                doThrow(new RuntimeException("DB down")).when(repository).save(any());

                assertThrows(ProcessingErrorException.class,
                        () -> service.execute("user-1", log, u -> {}));
            }
        }
    }
}