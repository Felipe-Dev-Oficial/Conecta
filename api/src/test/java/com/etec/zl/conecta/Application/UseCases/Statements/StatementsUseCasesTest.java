package com.etec.zl.conecta.Application.UseCases.Statements;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.NotificationService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Casos de Uso de Statements (Anúncios)")
class StatementsUseCasesTest {

    // ══════════════════════════════════════════════════════════════════════════
    // GerarAnuncioUseCase
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("GerarAnuncioUseCase")
    class GerarAnuncioTests {
        private StatementRepository repository;
        private StatementMapper mapper;
        private UserRepository userRepository;
        private NotificationService notificationService;
        private GerarAnuncioUseCase useCase;

        @BeforeEach
        void setUp() {
            repository           = mock(StatementRepository.class);
            mapper               = mock(StatementMapper.class);
            userRepository       = mock(UserRepository.class);
            notificationService  = mock(NotificationService.class);
            useCase              = new GerarAnuncioUseCase(repository, mapper, userRepository, notificationService);
        }

        @Test
        @DisplayName("deve mapear DTO, salvar o anúncio e enviar notificações")
        void geraAnuncioComSucesso() {
            var senderId    = "user-123";
            var dto         = mock(DTOAnuncio.class);
            var statement   = mock(Statement.class);
            var title       = mock(Content.class);
            var content     = mock(Content.class);
            var notificadores = List.of(mock(Notificador.class));

            when(dto.targetType()).thenReturn(mock(TargetType.class));
            when(dto.targetsId()).thenReturn(List.of("turma-1"));
            when(dto.title()).thenReturn(title);
            when(dto.content()).thenReturn(content);
            when(title.content()).thenReturn("Título do anúncio");
            when(content.content()).thenReturn("Corpo do anúncio");
            when(mapper.toStatement(senderId, dto)).thenReturn(statement);
            when(userRepository.findAllNotificadores(dto.targetType(), dto.targetsId()))
                    .thenReturn(notificadores);

            useCase.gerarAnuncio(senderId, dto);

            verify(repository, times(1)).save(statement);
            verify(notificationService, times(1))
                    .sendNotifications(notificadores, "Título do anúncio", "Corpo do anúncio");
        }

        @Test
        @DisplayName("não deve enviar notificação quando save lança exceção")
        void naoEnviaNotificacaoComFalhaNaSave() {
            var senderId  = "user-123";
            var dto       = mock(DTOAnuncio.class);
            var statement = mock(Statement.class);

            when(mapper.toStatement(senderId, dto)).thenReturn(statement);
            doThrow(new RuntimeException("falha no banco")).when(repository).save(statement);

            assertThrows(RuntimeException.class, () -> useCase.gerarAnuncio(senderId, dto));

            verify(notificationService, never()).sendNotifications(any(), any(), any());
        }
    }
    // ══════════════════════════════════════════════════════════════════════════
    // LerAnuncioUseCase
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("LerAnuncioUseCase")
    class LerAnuncioTests {
        private StatementRepository repository;
        private UserRepository userRepository;
        private StatementMapper mapper;
        private TryGetByUserService userService;
        private LerAnuncioUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(StatementRepository.class);
            userRepository = mock(UserRepository.class);
            mapper = mock(StatementMapper.class);
            userService = mock(TryGetByUserService.class);
            useCase = new LerAnuncioUseCase(repository, userRepository, mapper, userService);
        }

        @Test
        @DisplayName("Deve buscar usuário e retornar página de anúncios filtrados")
        void lerAnunciosDoUsuario() {
            var readerId = "user-123";
            var pageRequest = new PageRequest(0, 10);
            var user = mock(User.class);
            var pageResult = new PageResult<Statement>(List.of(), 0, 10, 0L, 0);

            when(userService.execute(any(), any())).thenReturn(user);
            when(repository.findStatements(any(), eq(pageRequest))).thenReturn(pageResult);

            var result = useCase.lerAnuncios(readerId, pageRequest);

            assertNotNull(result);
            verify(repository).findStatements(any(), eq(pageRequest));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Operações de Prioridade e Deleção (ModifyAndSave)
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("Usecases de Modificação (Prioridade e Apagar)")
    class ModificacaoTests {
        private VerifyIfExistsModifyAndSaveStatementsService modifyService;
        private UUID anuncioId;

        @BeforeEach
        void setUp() {
            modifyService = mock(VerifyIfExistsModifyAndSaveStatementsService.class);
            anuncioId = UUID.randomUUID();
        }

        @Test
        @DisplayName("ElevarPrioridade deve delegar para o service de modificação")
        void elevarPrioridade() {
            var useCase = new ElevarPrioridadeAnuncioUseCase(modifyService);
            useCase.elevarPrioridadeAnuncio(anuncioId);
            verify(modifyService).execute(eq(anuncioId), any(), any());
        }

        @Test
        @DisplayName("ApagarAnuncio deve delegar para o service de modificação")
        void apagarAnuncio() {
            var useCase = new ApagarAnuncioUseCase(modifyService);
            useCase.apagarAnuncio(anuncioId);
            verify(modifyService).execute(eq(anuncioId), any(), any());
        }
    }
}