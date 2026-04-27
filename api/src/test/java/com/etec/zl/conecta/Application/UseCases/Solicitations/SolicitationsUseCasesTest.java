package com.etec.zl.conecta.Application.UseCases.Solicitations;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTORequirement;
import com.etec.zl.conecta.Application.DTOs.Solicitations.DTOReturnRequirement;
import com.etec.zl.conecta.Application.Mappers.Solicitations.SolicitationMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.SolicitationRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Ports.Output.Services.NotificationService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@DisplayName("Solicitations Use Cases")
class SolicitationsUseCasesTest {

    // ─── Fixtures compartilhados ──────────────────────────────────────────────

    static final String      USER_ID         = "user-123";
    static final UUID        SOLICITATION_ID = UUID.randomUUID();
    static final PageRequest PAGE_REQUEST    = new PageRequest(0, 10);

    static Solicitation solicitationFactory(boolean solved) {
        return new Solicitation(
                SOLICITATION_ID,
                TypeRequirement.DECLARAÇÃO_DE_MATRICULA,
                null,
                solved,
                USER_ID,
                new Name("Arthur Henrique"),
                new Email("arthur@etec.sp.gov.br"),
                List.of("curso-1"),
                Instant.now()
        );
    }

    static DTOReturnRequirement dtoReturnFactory(Solicitation s) {
        return new DTOReturnRequirement(
                s.getId(),
                s.getTypeRequirement(),
                s.getOtherRequirement(),
                s.isSolved(),
                s.getCreatedAt()
        );
    }

    /**
     * Retorna um User mock e configura o tryGetByUserService para devolvê-lo.
     * Não faz stubs de getId/getNome/getEmail — o use case passa User direto
     * pro mapper, sem invocar esses getters.
     */
    static User mockUser(TryGetByUserService service) {
        User user = mock(User.class);
        given(service.execute(any(), any())).willReturn(user);
        return user;
    }

    static PageResult<Solicitation> pageOf(Solicitation... items) {
        return new PageResult<>(List.of(items), items.length, 1, 0, 1);
    }

    // ─── GetSelfSolicitationUseCase ──────────────────────────────────────────

    @Nested
    @DisplayName("GetSelfSolicitationUseCase")
    @ExtendWith(MockitoExtension.class)
    class GetSelfSolicitationUseCaseTests {

        @Mock SolicitationRepository solicitationRepository;
        @Mock SolicitationMapper     solicitationMapper;

        @InjectMocks GetSelfSolicitationUseCase useCase;

        @Test
        @DisplayName("should return paginated solicitations mapped to DTO for the requesting user")
        void shouldReturnSolicitationsForUser() {
            var solicitation = solicitationFactory(false);
            var dto          = dtoReturnFactory(solicitation);
            var page         = pageOf(solicitation);

            given(solicitationRepository.getSolicitationsByUser(USER_ID, PAGE_REQUEST)).willReturn(page);
            given(solicitationMapper.toDTOReturnRequirement(solicitation)).willReturn(dto);

            var result = useCase.getSolicitations(USER_ID, PAGE_REQUEST);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0)).isEqualTo(dto);
            then(solicitationRepository).should().getSolicitationsByUser(USER_ID, PAGE_REQUEST);
            then(solicitationMapper).should().toDTOReturnRequirement(solicitation);
        }

        @Test
        @DisplayName("should return empty page and skip mapper when user has no solicitations")
        void shouldReturnEmptyPageWithoutCallingMapper() {
            var emptyPage = new PageResult<Solicitation>(List.of(), 0, 0, 0, 1);

            given(solicitationRepository.getSolicitationsByUser(USER_ID, PAGE_REQUEST)).willReturn(emptyPage);

            var result = useCase.getSolicitations(USER_ID, PAGE_REQUEST);

            assertThat(result.content()).isEmpty();
            then(solicitationMapper).shouldHaveNoInteractions();
        }
    }

    // ─── GetSolicitationsSecretariaUseCase ───────────────────────────────────

    @Nested
    @DisplayName("GetSolicitationsSecretariaUseCase")
    @ExtendWith(MockitoExtension.class)
    class GetSolicitationsSecretariaUseCaseTests {

        @Mock SolicitationRepository solicitationRepository;

        @InjectMocks GetSolicitationsSecretariaUseCase useCase;

        @Test
        @DisplayName("should return solicitations filtered by search term")
        void shouldReturnFilteredSolicitations() {
            var search = "Arthur";
            var page   = pageOf(solicitationFactory(false));

            given(solicitationRepository.getSolicitationsBySearch(search, PAGE_REQUEST)).willReturn(page);

            var result = useCase.getSolicitationsSecretaria(search, PAGE_REQUEST);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).getIdSoliciter()).isEqualTo(USER_ID);
            then(solicitationRepository).should().getSolicitationsBySearch(search, PAGE_REQUEST);
        }

        @Test
        @DisplayName("should delegate to repository even when search is an empty string")
        void shouldDelegateWhenSearchIsEmpty() {
            var page = pageOf(solicitationFactory(false));

            given(solicitationRepository.getSolicitationsBySearch("", PAGE_REQUEST)).willReturn(page);

            var result = useCase.getSolicitationsSecretaria("", PAGE_REQUEST);

            assertThat(result.content()).hasSize(1);
            then(solicitationRepository).should().getSolicitationsBySearch("", PAGE_REQUEST);
        }

        @Test
        @DisplayName("should return empty page when no solicitations match")
        void shouldReturnEmptyPageWhenNoMatch() {
            var emptyPage = new PageResult<Solicitation>(List.of(), 0, 0, 0, 1);

            given(solicitationRepository.getSolicitationsBySearch("naoexiste", PAGE_REQUEST)).willReturn(emptyPage);

            var result = useCase.getSolicitationsSecretaria("naoexiste", PAGE_REQUEST);

            assertThat(result.content()).isEmpty();
        }
    }

    // ─── SendSolicitationUseCase ─────────────────────────────────────────────

    @Nested
    @DisplayName("SendSolicitationUseCase")
    @ExtendWith(MockitoExtension.class)
    class SendSolicitationUseCaseTests {

        @Mock SolicitationRepository solicitationRepository;
        @Mock UserRepository         userRepository;
        @Mock SolicitationMapper     solicitationMapper;
        @Mock TryGetByUserService    tryGetByUserService;
        @Mock NotificationService    notificationService;

        @InjectMocks SendSolicitationUseCase useCase;

        @Test
        @DisplayName("should save solicitation and fire push notification on success")
        void shouldSaveAndNotify() {
            var user          = mockUser(tryGetByUserService);
            var dto           = new DTORequirement(TypeRequirement.DECLARAÇÃO_DE_MATRICULA, null);
            var solicitation  = solicitationFactory(false);
            var notificadores = List.of(new Notificador(1L, "https://push.endpoint", "p256dh", "auth"));

            given(solicitationMapper.toRegister(user, dto)).willReturn(solicitation);
            given(userRepository.findAllNotificadoresSecretariaSolicitation()).willReturn(notificadores);
            willDoNothing().given(solicitationRepository).saveSolicitation(solicitation);
            willDoNothing().given(notificationService).sendNotifications(anyList(), anyString(), anyString());

            assertThatNoException().isThrownBy(() -> useCase.sendSolicitation(USER_ID, dto));

            then(solicitationRepository).should().saveSolicitation(solicitation);
            then(notificationService).should().sendNotifications(
                    eq(notificadores),
                    eq("Nova solicitação recebida"),
                    contains(USER_ID)
            );
        }

        @Test
        @DisplayName("should always include userId in the notification message body")
        void shouldIncludeUserIdInNotificationMessage() {
            var user         = mockUser(tryGetByUserService);
            var dto          = new DTORequirement(TypeRequirement.CERTIFICADO_ATUAL, null);
            var solicitation = solicitationFactory(false);

            given(solicitationMapper.toRegister(user, dto)).willReturn(solicitation);
            given(userRepository.findAllNotificadoresSecretariaSolicitation()).willReturn(List.of());
            willDoNothing().given(solicitationRepository).saveSolicitation(any());
            willDoNothing().given(notificationService).sendNotifications(anyList(), anyString(), anyString());

            useCase.sendSolicitation(USER_ID, dto);

            then(notificationService).should().sendNotifications(
                    anyList(),
                    anyString(),
                    argThat(msg -> msg.contains(USER_ID))
            );
        }

        @Test
        @DisplayName("should save OUTRO type solicitation passing description through mapper")
        void shouldSaveOutroSolicitation() {
            var user         = mockUser(tryGetByUserService);
            var dto          = new DTORequirement(TypeRequirement.OUTRO, "Preciso de atestado de frequência");
            var solicitation = solicitationFactory(false);

            given(solicitationMapper.toRegister(user, dto)).willReturn(solicitation);
            given(userRepository.findAllNotificadoresSecretariaSolicitation()).willReturn(List.of());
            willDoNothing().given(solicitationRepository).saveSolicitation(any());
            willDoNothing().given(notificationService).sendNotifications(anyList(), anyString(), anyString());

            assertThatNoException().isThrownBy(() -> useCase.sendSolicitation(USER_ID, dto));

            then(solicitationRepository).should().saveSolicitation(any());
        }

        @Test
        @DisplayName("should still call sendNotifications even when notificadores list is empty")
        void shouldCallNotifyEvenWithEmptyList() {
            var user         = mockUser(tryGetByUserService);
            var dto          = new DTORequirement(TypeRequirement.CERTIFICADO_ATUAL, null);
            var solicitation = solicitationFactory(false);

            given(solicitationMapper.toRegister(user, dto)).willReturn(solicitation);
            given(userRepository.findAllNotificadoresSecretariaSolicitation()).willReturn(List.of());
            willDoNothing().given(solicitationRepository).saveSolicitation(any());
            willDoNothing().given(notificationService).sendNotifications(anyList(), anyString(), anyString());

            useCase.sendSolicitation(USER_ID, dto);

            then(notificationService).should().sendNotifications(eq(List.of()), anyString(), anyString());
        }
    }

    // ─── SolveSolicitationUseCase ─────────────────────────────────────────────

    @Nested
    @DisplayName("SolveSolicitationUseCase")
    @ExtendWith(MockitoExtension.class)
    class SolveSolicitationUseCaseTests {

        @Mock SolicitationRepository solicitationRepository;

        @InjectMocks SolveSolicitationUseCase useCase;

        @Test
        @DisplayName("should mark solicitation as solved and persist the change")
        void shouldSolveAndSave() {
            var solicitation = solicitationFactory(false);

            given(solicitationRepository.getSolicitationById(SOLICITATION_ID))
                    .willReturn(Optional.of(solicitation));
            willDoNothing().given(solicitationRepository).saveSolicitation(any());

            assertThatNoException().isThrownBy(() -> useCase.solveSolicitation(SOLICITATION_ID));

            assertThat(solicitation.isSolved()).isTrue();
            then(solicitationRepository).should().saveSolicitation(solicitation);
        }

        @Test
        @DisplayName("should throw InvalidDataException and never save when solicitation not found")
        void shouldThrowWhenNotFound() {
            given(solicitationRepository.getSolicitationById(SOLICITATION_ID))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.solveSolicitation(SOLICITATION_ID))
                    .isInstanceOf(InvalidDataException.class);

            then(solicitationRepository).should(never()).saveSolicitation(any());
        }

        @Test
        @DisplayName("should throw and never save when solicitation is already solved")
        void shouldThrowAndNotSaveWhenAlreadySolved() {
            var solvedSolicitation = solicitationFactory(true);

            given(solicitationRepository.getSolicitationById(SOLICITATION_ID))
                    .willReturn(Optional.of(solvedSolicitation));

            assertThatThrownBy(() -> useCase.solveSolicitation(SOLICITATION_ID))
                    .isInstanceOf(InvalidDataException.class);

            then(solicitationRepository).should(never()).saveSolicitation(any());
        }
    }
}