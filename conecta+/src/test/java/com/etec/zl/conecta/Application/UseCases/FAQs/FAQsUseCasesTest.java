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
@DisplayName("FAQs UseCases")
class FAQsUseCasesTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(String id) {
        return new User(id, new Name("Ana Lima"), new Email("ana@etec.com"),
                new PhoneNumber("11987654321"), new Password("Etec@1234"),
                Tipo.SECRETARIA, new ArrayList<>());
    }

    private FAQ buildFAQ(String authorId) {
        return new FAQ(UUID.randomUUID(), "Pergunta?", "Resposta.",
                authorId, StatusFAQ.RASCUNHO, Instant.now(), null, Prioridade.MEDIA);
    }

    private PageResult<FAQ> pageOf(FAQ... faqs) {
        return new PageResult<>(List.of(faqs), 0, 10, faqs.length, 1);
    }

    // ─── LerFAQUseCase ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("LerFAQUseCase")
    class LerFAQTest {

        @Mock FAQRepository faqRepository;
        @Mock FAQMapper mapper;

        LerFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new LerFAQUseCase(faqRepository, mapper);
        }

        @Test
        @DisplayName("Deve retornar PageResult de DTOReturnFAQ")
        void lerFAQs_sucesso() {
            var faq = buildFAQ("autor-1");
            var dto = new DTOReturnFAQ("Pergunta?", "Resposta.");
            var req = new PageRequest(0, 10);

            when(faqRepository.getAll(req)).thenReturn(pageOf(faq));
            when(mapper.toReturn(faq)).thenReturn(dto);

            var result = useCase.lerFAQs(req);

            assertEquals(1, result.content().size());
            assertEquals(dto, result.content().get(0));
        }

        @Test
        @DisplayName("Deve retornar PageResult vazio quando não há FAQs")
        void lerFAQs_vazio() {
            var req = new PageRequest(0, 10);
            when(faqRepository.getAll(req)).thenReturn(new PageResult<>(List.of(), 0, 10, 0, 0));

            var result = useCase.lerFAQs(req);

            assertTrue(result.content().isEmpty());
        }
    }

//    // ─── LerFAQsSecretariaUseCase ─────────────────────────────────────────────
//
//    @Nested
//    @DisplayName("LerFAQsSecretariaUseCase")
//    class LerFAQsSecretariaTest {
//
//        @Mock FAQRepository faqRepository;
//        @Mock UserRepository userRepository;
//        @Mock FAQMapper mapper;
//
//        LerFAQsSecretariaUseCase useCase;
//
//        @BeforeEach
//        void setUp() {
//            useCase = new LerFAQsSecretariaUseCase(faqRepository, userRepository, mapper);
//        }
//
//        @Test
//        @DisplayName("Deve retornar PageResult de DTOReturnFAQSecretaria")
//        void lerFAQsSecretaria_sucesso() {
//            var autor = buildUser("autor-1");
//            var faq = buildFAQ("autor-1");
//            var dto = new DTOReturnFAQSecretaria(faq.getId(), autor.getNome(),
//                    "Pergunta?", "Resposta.", Prioridade.MEDIA);
//            var req = new PageRequest(0, 10);
//
//            when(faqRepository.getAll(req)).thenReturn(pageOf(faq));
//            when(userRepository.findById("autor-1")).thenReturn(Optional.of(autor));
//            when(mapper.toReturnSecretaria(autor.getNome(), faq)).thenReturn(dto);
//
//            var result = useCase.lerFAQsSecretaria(req);
//
//            assertEquals(1, result.content().size());
//            verify(mapper).toReturnSecretaria(autor.getNome(), faq);
//        }
//
//        @Test
//        @DisplayName("Deve lançar UserNotFoundException se autor da FAQ não existe")
//        void lerFAQsSecretaria_autorNaoEncontrado() {
//            var faq = buildFAQ("autor-inexistente");
//            var req = new PageRequest(0, 10);
//
//            when(faqRepository.getAll(req)).thenReturn(pageOf(faq));
//            when(userRepository.findById("autor-inexistente")).thenReturn(Optional.empty());
//
//            assertThrows(UserNotFoundException.class, () -> useCase.lerFAQsSecretaria(req));
//        }
//    }

    // ─── EscreverFAQUseCase ───────────────────────────────────────────────────

    @Nested
    @DisplayName("EscreverFAQUseCase")
    class EscreverFAQTest {

        @Mock FAQRepository faqRepository;
        @Mock UserRepository userRepository;
        @Mock FAQMapper mapper;

        EscreverFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new EscreverFAQUseCase(faqRepository, userRepository, mapper);
        }

        @Test
        @DisplayName("Deve criar e salvar FAQ")
        void escreverFAQ_sucesso() {
            var user = buildUser("autor-1");
            var dto = new DTORegisterFAQ("Pergunta?", "Resposta.", Prioridade.ALTA);
            var faq = buildFAQ("autor-1");

            when(userRepository.findById("autor-1")).thenReturn(Optional.of(user));
            when(mapper.toRegister("autor-1", dto)).thenReturn(faq);

            assertDoesNotThrow(() -> useCase.escreverFAQ("autor-1", dto));
            verify(faqRepository).save(faq);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se autor não existe")
        void escreverFAQ_autorNaoEncontrado() {
            var dto = new DTORegisterFAQ("Pergunta?", "Resposta.", Prioridade.ALTA);
            when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.escreverFAQ("inexistente", dto));
            verify(faqRepository, never()).save(any());
        }
    }

    // ─── AlterarFAQUseCase ────────────────────────────────────────────────────

    @Nested
    @DisplayName("AlterarFAQUseCase")
    class AlterarFAQTest {

        @Mock VerifyIfExistsModifyAndSaveFAQsService service;

        AlterarFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new AlterarFAQUseCase(service);
        }

        @Test
        @DisplayName("Deve chamar service com a modificação correta")
        void alterarFAQ_chamaService() {
            var id = UUID.randomUUID();
            var dto = new DTOUpdateFaq("Nova pergunta", "Nova resposta");

            assertDoesNotThrow(() -> useCase.alterarFAQ(id, dto));
            verify(service).execute(eq(id), any(), any());
        }
    }

    // ─── ApagarFAQUseCase ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("ApagarFAQUseCase")
    class ApagarFAQTest {

        @Mock VerifyIfExistsModifyAndSaveFAQsService service;

        ApagarFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ApagarFAQUseCase(service);
        }

        @Test
        @DisplayName("Deve chamar service com FAQ::apagarFAQ")
        void apagarFAQ_chamaService() {
            var id = UUID.randomUUID();

            assertDoesNotThrow(() -> useCase.apagarFAQ(id));
            verify(service).execute(eq(id), any(), any());
        }
    }

    // ─── PublicarFAQUseCase ───────────────────────────────────────────────────

    @Nested
    @DisplayName("PublicarFAQUseCase")
    class PublicarFAQTest {

        @Mock VerifyIfExistsModifyAndSaveFAQsService service;

        PublicarFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new PublicarFAQUseCase(service);
        }

        @Test
        @DisplayName("Deve chamar service com FAQ::publicar")
        void publicarFAQ_chamaService() {
            var id = UUID.randomUUID();

            assertDoesNotThrow(() -> useCase.publicarFAQ(id));
            verify(service).execute(eq(id), any(), any());
        }
    }

    // ─── AumentarPrioridadeFAQUseCase ─────────────────────────────────────────

    @Nested
    @DisplayName("AumentarPrioridadeFAQUseCase")
    class AumentarPrioridadeTest {

        @Mock VerifyIfExistsModifyAndSaveFAQsService service;

        AumentarPrioridadeFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new AumentarPrioridadeFAQUseCase(service);
        }

        @Test
        @DisplayName("Deve chamar service com FAQ::elevarPrioridade")
        void elevarPrioridade_chamaService() {
            var id = UUID.randomUUID();

            assertDoesNotThrow(() -> useCase.elevarPrioridadeFAQ(id));
            verify(service).execute(eq(id), any(), any());
        }
    }

    // ─── DiminuirPrioridadeFAQUseCase ─────────────────────────────────────────

    @Nested
    @DisplayName("DiminuirPrioridadeFAQUseCase")
    class DiminuirPrioridadeTest {

        @Mock VerifyIfExistsModifyAndSaveFAQsService service;

        DiminuirPrioridadeFAQUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new DiminuirPrioridadeFAQUseCase(service);
        }

        @Test
        @DisplayName("Deve chamar service com FAQ::reduzirPrioridade")
        void diminuirPrioridade_chamaService() {
            var id = UUID.randomUUID();

            assertDoesNotThrow(() -> useCase.diminuirPrioridadeFAQ(id));
            verify(service).execute(eq(id), any(), any());
        }
    }
}