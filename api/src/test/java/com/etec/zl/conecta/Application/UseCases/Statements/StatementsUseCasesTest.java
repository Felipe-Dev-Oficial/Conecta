//package com.etec.zl.conecta.Application.UseCases.Statements;
//
//import com.etec.zl.conecta.Application.DTOs.Statements.DTOAlteraAnuncio;
//import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
//import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
//import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
//import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
//import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
//import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
//import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
//import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
//import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
//import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
//import com.etec.zl.conecta.Domain.ValueObjects.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("Statements UseCases")
//class StatementsUseCasesTest {
//
//    // ─── Helpers ──────────────────────────────────────────────────────────────
//
//    private Statement buildStatement(String senderId) {
//        return new Statement(UUID.randomUUID(), senderId,
//                new Content("Título"), Instant.now(),
//                new Content("Corpo"), null,
//                Prioridade.MEDIA, false, Status.ON, TargetVO.paraTodos());
//    }
//
//    private PageResult<Statement> pageOf(Statement... statements) {
//        return new PageResult<>(List.of(statements), 0, 10, statements.length, 1);
//    }
//
//    // ─── LerAnuncioUseCase ────────────────────────────────────────────────────
//
//    @Nested
//    @DisplayName("LerAnuncioUseCase")
//    class LerAnuncioTest {
//
//        @Mock StatementRepository statementRepository;
//        @Mock UserRepository userRepository;
//        @Mock StatementMapper mapper;
//        @Mock TryGetByUserService userService;
//
//        LerAnuncioUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new LerAnuncioUseCase(statementRepository, userRepository, mapper, userService);
//        }
//
//        @Test
//        @DisplayName("Deve retornar PageResult de anúncios mapeados")
//        void lerAnuncios_sucesso() {
//            var nome = new Name("Carlos Lima");
//            var userDto = new DTORetornoNormal("u1", nome, Tipo.ALUNO);
//            var statement = buildStatement("u1");
//            var turmas = List.of(UUID.randomUUID());
//            var dtoReturn = new DTORetornoAnuncio(nome, statement.getTitle(),
//                    statement.getTimestamp(), false);
//            var req = new PageRequest(0, 10);
//
//            when(userService.execute(any(), any())).thenReturn(userDto);
//            when(userRepository.retornarTurmasRelacionadas("u1")).thenReturn(turmas);
//            when(statementRepository.findStatements(any(), eq(req))).thenReturn(pageOf(statement));
//            when(mapper.toDTOReturn(statement, nome)).thenReturn(dtoReturn);
//
//            var result = useCase.lerAnuncios("u1", req);
//
//            assertEquals(1, result.content().size());
//            verify(mapper).toDTOReturn(statement, nome);
//        }
//
//        @Test
//        @DisplayName("Deve lançar UserNotFoundException se usuário não existe")
//        void lerAnuncios_usuarioNaoEncontrado() {
//            var req = new PageRequest(0, 10);
//            when(userService.execute(any(), any())).thenThrow(new UserNotFoundException());
//
//            assertThrows(UserNotFoundException.class,
//                    () -> useCase.lerAnuncios("inexistente", req));
//            verify(statementRepository, never()).findStatements(any(), any());
//        }
//    }
//
//    // ─── LerAnuncioSecretariaUseCase ──────────────────────────────────────────
//
////    @Nested
////    @DisplayName("LerAnuncioSecretariaUseCase")
////    class LerAnuncioSecretariaTest {
////
////        @Mock StatementRepository statementRepository;
////        @Mock StatementMapper mapper;
////
////        LerAnuncioSecretariaUseCase useCase;
////
////        @BeforeEach
////        void setUp() {
////            useCase = new LerAnuncioSecretariaUseCase(statementRepository, mapper);
////        }
//
////        @Test
////        @DisplayName("Deve retornar PageResult de statements para secretaria")
////        void lerAnunciosSecretaria_sucesso() {
////            var statement = buildStatement("s1");
////            var req = new PageRequest(0, 10);
////
////            when(mapper.toDTOLeitura(Tipo.SECRETARIA, null)).thenCallRealMethod();
////            when(statementRepository.findStatements(any(), eq(req))).thenReturn(pageOf(statement));
////
////            var result = useCase.lerAnunciosSecretaria(req);
////
////            assertEquals(1, result.content().size());
////        }
//    }
//
//    // ─── GerarAnuncioUseCase ──────────────────────────────────────────────────
//
//    @Nested
//    @DisplayName("GerarAnuncioUseCase")
//    class GerarAnuncioTest {
//
//        @Mock StatementRepository statementRepository;
//        @Mock StatementMapper mapper;
//
//        GerarAnuncioUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new GerarAnuncioUseCase(statementRepository, mapper);
//        }
//
//        @Test
//        @DisplayName("Deve criar e salvar anúncio")
//        void gerarAnuncio_sucesso() {
//            var dto = new DTOAnuncio(new Content("Título"), new Content("Corpo"),
//                    null, Prioridade.MEDIA, TargetType.GERAL, null);
//            var statement = buildStatement("sender-1");
//
//            when(mapper.toStatement("sender-1", dto)).thenReturn(statement);
//
//            assertDoesNotThrow(() -> useCase.gerarAnuncio("sender-1", dto));
//            verify(statementRepository).save(statement);
//        }
//    }
//
//    // ─── ApagarAnuncioUseCase ─────────────────────────────────────────────────
//
//    @Nested
//    @DisplayName("ApagarAnuncioUseCase")
//    class ApagarAnuncioTest {
//
//        @Mock VerifyIfExistsModifyAndSaveStatementsService service;
//
//        ApagarAnuncioUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new ApagarAnuncioUseCase(service);
//        }
//
//        @Test
//        @DisplayName("Deve chamar service com Statement::apagarAnuncio")
//        void apagarAnuncio_chamaService() {
//            var id = UUID.randomUUID();
//
//            assertDoesNotThrow(() -> useCase.apagarAnuncio(id));
//            verify(service).execute(eq(id), any(), any());
//        }
//    }
//
//    // ─── AlterarAnuncioUseCase ────────────────────────────────────────────────
//
//    @Nested
//    @DisplayName("AlterarAnuncioUseCase")
//    class AlterarAnuncioTest {
//
//        @Mock VerifyIfExistsModifyAndSaveStatementsService service;
//
//        AlterarAnuncioUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new AlterarAnuncioUseCase(service);
//        }
//
//        @Test
//        @DisplayName("Deve chamar service com todas as alterações do DTO")
//        void alterarAnuncio_chamaService() {
//            var id = UUID.randomUUID();
//            var dto = new DTOAlteraAnuncio(new Content("Novo título"),
//                    new Content("Novo corpo"), null, Prioridade.ALTA);
//
//            assertDoesNotThrow(() -> useCase.alterarAnuncio(id, dto));
//            verify(service).execute(eq(id), any(), any());
//        }
//    }
//
//    // ─── ElevarPrioridadeAnuncioUseCase ───────────────────────────────────────
//
//    @Nested
//    @DisplayName("ElevarPrioridadeAnuncioUseCase")
//    class ElevarPrioridadeTest {
//
//        @Mock VerifyIfExistsModifyAndSaveStatementsService service;
//
//        ElevarPrioridadeAnuncioUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new ElevarPrioridadeAnuncioUseCase(service);
//        }
//
//        @Test
//        @DisplayName("Deve chamar service com Statement::elevarPrioridade")
//        void elevarPrioridade_chamaService() {
//            var id = UUID.randomUUID();
//
//            assertDoesNotThrow(() -> useCase.elevarPrioridadeAnuncio(id));
//            verify(service).execute(eq(id), any(), any());
//        }
//    }
//
//    // ─── ReduzirPrioridadeAnuncioUseCase ──────────────────────────────────────
//
//    @Nested
//    @DisplayName("ReduzirPrioridadeAnuncioUseCase")
//    class ReduzirPrioridadeTest {
//
//        @Mock VerifyIfExistsModifyAndSaveStatementsService service;
//
//        ReduzirPrioridadeAnuncioUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new ReduzirPrioridadeAnuncioUseCase(service);
//        }
//
//        @Test
//        @DisplayName("Deve chamar service com Statement::reduzirPrioridade")
//        void reduzirPrioridade_chamaService() {
//            var id = UUID.randomUUID();
//
//            assertDoesNotThrow(() -> useCase.reduzirPrioridadeAnuncio(id));
//            verify(service).execute(eq(id), any(), any());
//        }
//    }
//}