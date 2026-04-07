package com.etec.zl.conecta.Application.UseCases.FAQs;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOUpdateFaq;
import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
import com.etec.zl.conecta.Domain.ValueObjects.Tipo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FAQsUseCasesTest {

    @Nested
    @DisplayName("EscreverFAQUseCase")
    class EscreverFAQUseCaseTests {

        @Mock private FAQRepository repository;
        @Mock private UserRepository userRepository;
        @Mock private FAQMapper mapper;
        @Mock private User user;

        private EscreverFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new EscreverFAQUseCase(repository, userRepository, mapper);
        }

        @Test
        @DisplayName("deve salvar um novo FAQ quando o usuário é encontrado")
        void deveSalvarFAQ() {
            String userId = "user-123";
            DTORegisterFAQ dto = new DTORegisterFAQ("Pergunta?", "Resposta", null);
            User user = mock(User.class);
            FAQ faq = mock(FAQ.class);

            when(user.getId()).thenReturn(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(mapper.toRegister(userId, dto)).thenReturn(faq);

            useCase.escreverFAQ(userId, dto);

            verify(repository, times(1)).save(faq);
        }
    }

    @Nested
    @DisplayName("AlterarFAQUseCase")
    class AlterarFAQUseCaseTests {

        @Mock private VerifyIfExistsModifyAndSaveFAQsService service;
        private AlterarFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new AlterarFAQUseCase(service);
        }

        @Test
        @DisplayName("deve delegar a alteração para o serviço de verificação")
        void deveDelegarParaServico() {
            UUID faqId = UUID.randomUUID();
            DTOUpdateFaq dto = new DTOUpdateFaq("Nova Pergunta", "Nova Resposta");

            useCase.alterarFAQ(faqId, dto);

            verify(service, times(1)).execute(eq(faqId), any(), any());
        }
    }

    @Nested
    @DisplayName("LerFAQUseCase")
    class LerFAQUseCaseTests {

        @Mock private FAQRepository repository;
        @Mock private FAQMapper mapper;
        private LerFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new LerFAQUseCase(repository, mapper);
        }

        @Test
        @DisplayName("deve retornar lista de FAQs ativos mapeados para DTO")
        void deveRetornarFAQsAtivos() {
            PageRequest pageable = new PageRequest(0, 10);
            FAQ faq = mock(FAQ.class);
            PageResult<FAQ> pageResult = new PageResult<>(List.of(faq), 1, 10, 1, 0);

            when(repository.getAllActives(pageable)).thenReturn(pageResult);
            when(mapper.toReturn(any(FAQ.class))).thenReturn(new DTOReturnFAQ("Q", "A"));

            useCase.lerFAQs(pageable);

            verify(repository, times(1)).getAllActives(pageable);
            verify(mapper, times(1)).toReturn(any(FAQ.class));
        }
    }

    @Nested
    @DisplayName("LerFAQsSecretariaUseCase")
    class LerFAQsSecretariaUseCaseTests {

        @Mock private FAQRepository repository;
        private LerFAQsSecretariaUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new LerFAQsSecretariaUseCase(repository);
        }

        @Test
        @DisplayName("deve retornar todos os FAQs sem mapeamento para a secretaria")
        void deveRetornarTodosOsFAQs() {
            PageRequest pageable = new PageRequest(0, 10);

            useCase.lerFAQsSecretaria(pageable);

            verify(repository, times(1)).getAll(pageable);
        }
    }
}