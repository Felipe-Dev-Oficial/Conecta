package com.etec.zl.conecta.Application.Services.Services;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.EmailService;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Application.Services.Services.Users.*;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("Application Services")
class ApplicationServicesTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(String id, Tipo tipo) {
        return new User(id, new Name("Ana Lima"), new Email("ana@etec.com"),
                new PhoneNumber("11987654321"), new Password("Etec@1234"), tipo, new ArrayList<>());
    }

    private FAQ buildFAQ() {
        return new FAQ(UUID.randomUUID(), "Pergunta?", "Resposta.",
                "autor-1", StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.MEDIA);
    }

    private Statement buildStatement() {
        return new Statement(UUID.randomUUID(), "sender-1",
                new Content("Título"), Instant.now(), new Content("Corpo"),
                null, Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos());
    }

    private PageResult<User> pageOfUsers(User... users) {
        return new PageResult<>(List.of(users), 0, 10, users.length, 1);
    }

    // ─── TryGetUsersService ───────────────────────────────────────────────────

    @Nested
    @DisplayName("TryGetUsersService")
    class TryGetUsersServiceTest {

        @Mock UserMapper mapper;
        TryGetUsersService service;

        @BeforeEach
        void setUp() { service = new TryGetUsersService(mapper); }

        @Test
        @DisplayName("Deve mapear Page<User> para PageResult<DTORetornoNormal>")
        void execute_mapeiaCorretamente() {
            var user = buildUser("u1", Tipo.ALUNO);
            var dto = new DTORetornoNormal("u1", user.getNome(), Tipo.ALUNO);
            var page = pageOfUsers(user);

            when(mapper.toDTOReturn(user)).thenReturn(dto);

            var result = service.execute(() -> page, null);

            assertEquals(1, result.content().size());
            assertEquals(dto, result.content().get(0));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha do supplier")
        void execute_falhaSupplier() {
            assertThrows(ProcessingErrorException.class,
                    () -> service.execute(() -> { throw new RuntimeException("DB"); }, null));
        }
    }

    // ─── TryGetUsersSecretariaService ─────────────────────────────────────────

    @Nested
    @DisplayName("TryGetUsersSecretariaService")
    class TryGetUsersSecretariaServiceTest {

        @Mock UserMapper mapper;
        TryGetUsersSecretariaService service;

        @BeforeEach
        void setUp() { service = new TryGetUsersSecretariaService(mapper); }

        @Test
        @DisplayName("Deve mapear Page<User> para PageResult<DTORetornoSecretaria>")
        void execute_mapeiaCorretamente() {
            var user = buildUser("u1", Tipo.SECRETARIA);
            var dto = new DTORetornoSecretaria("u1", user.getNome(), user.getEmail(),
                    user.getNumero(), Tipo.SECRETARIA);
            var page = pageOfUsers(user);

            when(mapper.toDTOReturnSecretaria(user)).thenReturn(dto);

            var result = service.execute(() -> page, null);

            assertEquals(1, result.content().size());
            assertEquals(dto, result.content().get(0));
        }
    }

    // ─── TryGetByUserService ──────────────────────────────────────────────────

//    @Nested
//    @DisplayName("TryGetByUserService")
//    class TryGetByUserServiceTest {
//
//        @Mock UserMapper mapper;
//        @Mock UserRepository repository;
//        TryGetByUserService service;
//
//        @BeforeEach
//        void setUp() { service = new TryGetByUserService(mapper); }
//
//        @Test
//        @DisplayName("Deve retornar DTORetornoNormal quando usuário existe")
//        void execute_usuarioEncontrado() {
//            var user = buildUser("u1", Tipo.ALUNO);
//            var dto = new DTORetornoNormal("u1", user.getNome(), Tipo.ALUNO);
//
//            when(mapper.toDTOReturn(user)).thenReturn(dto);
//
//            var result = service.execute(() -> Optional.of(user), null);
//
//            assertEquals(dto, result);
//        }
//
//        @Test
//        @DisplayName("Deve lançar UserNotFoundException quando usuário não existe")
//        void execute_usuarioNaoEncontrado() {
//            assertThrows(UserNotFoundException.class,
//                    () -> service.execute(Optional::empty, null));
//        }
//    }

    // ─── TryGetByUserSecretariaService ────────────────────────────────────────

    @Nested
    @DisplayName("TryGetByUserSecretariaService")
    class TryGetByUserSecretariaServiceTest {

        @Mock UserMapper mapper;
        TryGetByUserSecretariaService service;

        @BeforeEach
        void setUp() { service = new TryGetByUserSecretariaService(mapper); }

        @Test
        @DisplayName("Deve retornar DTORetornoSecretaria quando usuário existe")
        void execute_usuarioEncontrado() {
            var user = buildUser("u1", Tipo.SECRETARIA);
            var dto = new DTORetornoSecretaria("u1", user.getNome(), user.getEmail(),
                    user.getNumero(), Tipo.SECRETARIA);

            when(mapper.toDTOReturnSecretaria(user)).thenReturn(dto);

            var result = service.execute(() -> Optional.of(user), null);

            assertEquals(dto, result);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException quando usuário não existe")
        void execute_usuarioNaoEncontrado() {
            assertThrows(UserNotFoundException.class,
                    () -> service.execute(Optional::empty, null));
        }
    }

    // ─── TrySaveUserService ───────────────────────────────────────────────────

//    @Nested
//    @DisplayName("TrySaveUserService")
//    class TrySaveUserServiceTest {
//
//        @Mock UserRepository repository;
//        @Mock UserMapper mapper;
//        TrySaveUserService service;
//
//        @BeforeEach
//        void setUp() { service = new TrySaveUserService(repository, mapper); }
//
//        @Test
//        @DisplayName("Deve converter e salvar o usuário")
//        void execute_salvaUsuario() {
//            var user = buildUser("u1", Tipo.ALUNO);
//            var dto = new DTOCadastro("u1", user.getNome(), user.getEmail(),
//                    user.getNumero(), user.getSenha(), Tipo.ALUNO, new ArrayList<>());
//
//            when(mapper.toRegister(dto)).thenReturn(user);
//
//            assertDoesNotThrow(() -> service.execute(dto, null));
//            verify(repository).save(user);
//        }
//    }

    // ─── VerifyIfExistsModifyAndSaveUsersService ──────────────────────────────

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveUsersService")
    class VerifyUsersServiceTest {

        @Mock UserRepository repository;
        VerifyIfExistsModifyAndSaveUsersService service;

        @BeforeEach
        void setUp() { service = new VerifyIfExistsModifyAndSaveUsersService(repository); }

        @Test
        @DisplayName("Deve aplicar modificação e salvar usuário encontrado")
        void execute_modificaESalva() {
            var user = buildUser("u1", Tipo.ALUNO);
            when(repository.findById("u1")).thenReturn(Optional.of(user));

            assertDoesNotThrow(() -> service.execute("u1", null, u -> {}));
            verify(repository).save(user);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se usuário não existe")
        void execute_usuarioNaoEncontrado() {
            when(repository.findById("inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> service.execute("inexistente", null, u -> {}));
            verify(repository, never()).save(any());
        }
    }

    // ─── VerifyIfExistsModifyAndSaveFAQsService ───────────────────────────────

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveFAQsService")
    class VerifyFAQsServiceTest {

        @Mock FAQRepository repository;
        VerifyIfExistsModifyAndSaveFAQsService service;

        @BeforeEach
        void setUp() { service = new VerifyIfExistsModifyAndSaveFAQsService(repository); }

        @Test
        @DisplayName("Deve aplicar modificação e salvar FAQ encontrado")
        void execute_modificaESalva() {
            var faq = buildFAQ();
            when(repository.getById(faq.getId())).thenReturn(Optional.of(faq));

            assertDoesNotThrow(() -> service.execute(faq.getId(), f -> {}, null));
            verify(repository).save(faq);
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException se FAQ não existe")
        void execute_faqNaoEncontrado() {
            var id = UUID.randomUUID();
            when(repository.getById(id)).thenReturn(Optional.empty());

            assertThrows(InvalidDataException.class,
                    () -> service.execute(id, f -> {}, null));
            verify(repository, never()).save(any());
        }
    }

    // ─── VerifyIfExistsModifyAndSaveStatementsService ─────────────────────────

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveStatementsService")
    class VerifyStatementsServiceTest {

        @Mock StatementRepository repository;
        VerifyIfExistsModifyAndSaveStatementsService service;

        @BeforeEach
        void setUp() { service = new VerifyIfExistsModifyAndSaveStatementsService(repository); }

        @Test
        @DisplayName("Deve aplicar modificação e salvar Statement encontrado")
        void execute_modificaESalva() {
            var statement = buildStatement();
            when(repository.findById(statement.getId())).thenReturn(Optional.of(statement));

            assertDoesNotThrow(() -> service.execute(statement.getId(), s -> {}, null));
            verify(repository).save(statement);
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException se Statement não existe")
        void execute_statementNaoEncontrado() {
            var id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(InvalidDataException.class,
                    () -> service.execute(id, s -> {}, null));
            verify(repository, never()).save(any());
        }
    }

    // ─── StartChangeService ───────────────────────────────────────────────────

    @Nested
    @DisplayName("StartChangeService")
    class StartChangeServiceTest {

        @Mock UserRepository repository;
        @Mock EmailService emailService;
        StartChangeService service;

        @BeforeEach
        void setUp() { service = new StartChangeService(repository, emailService); }

        @Test
        @DisplayName("Deve iniciar processo de troca e enviar email")
        void execute_sucesso() {
            var user = buildUser("u1", Tipo.ALUNO);
            var updater = TokenUpdater.Start();
            when(repository.findById("u1")).thenReturn(Optional.of(user));

            assertDoesNotThrow(() -> service.execute(
                    "u1",
                    u -> {},
                    u -> updater,
                    "Assunto do email",
                    null
            ));

            verify(emailService).send(contains(updater.token()), eq(user.getEmail()), eq("Assunto do email"));
            verify(repository).save(user);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se usuário não existe")
        void execute_usuarioNaoEncontrado() {
            when(repository.findById("inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> service.execute(
                    "inexistente", u -> {}, u -> null, "Assunto", null
            ));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException se token extractor retornar null")
        void execute_tokenNulo() {
            var user = buildUser("u1", Tipo.ALUNO);
            when(repository.findById("u1")).thenReturn(Optional.of(user));

            assertThrows(ProcessingErrorException.class, () -> service.execute(
                    "u1", u -> {}, u -> null, "Assunto", null
            ));
        }
    }
}