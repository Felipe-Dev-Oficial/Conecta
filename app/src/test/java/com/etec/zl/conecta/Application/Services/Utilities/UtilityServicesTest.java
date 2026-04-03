package com.etec.zl.conecta.Application.Services.Utilities;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Utility Services")
class UtilityServicesTest {

    private static final Logger log = LoggerFactory.getLogger(UtilityServicesTest.class);

    // ─── TryGetService ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("TryGetService")
    class TryGetServiceTest {

        @Test
        @DisplayName("Deve retornar PageResult quando supplier executa com sucesso")
        void execute_sucesso() {
            var page = new PageResult<>(List.of("item1", "item2"), 0, 10, 2L, 1);

            var result = TryGetService.execute(() -> page, log);

            assertEquals(page, result);
            assertEquals(2, result.content().size());
        }

        @Test
        @DisplayName("Deve retornar PageResult vazio sem lançar exceção")
        void execute_paginaVazia_sucesso() {
            var page = new PageResult<String>(List.of(), 0, 10, 0L, 0);

            var result = TryGetService.execute(() -> page, log);

            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException quando supplier lança RuntimeException")
        void execute_runtimeException_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> TryGetService.execute(
                            () -> { throw new RuntimeException("DB timeout"); },
                            log
                    ));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException quando supplier lança RuntimeException encapsulando Exception")
        void execute_checkedException_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> TryGetService.execute(
                            () -> { throw new RuntimeException(new Exception("IO error")); },
                            log
                    ));
        }

        @Test
        @DisplayName("Deve preservar metadados de paginação no resultado")
        void execute_preservaMetadados() {
            var page = new PageResult<>(List.of("x"), 2, 5, 100L, 20);

            var result = TryGetService.execute(() -> page, log);

            assertEquals(2, result.page());
            assertEquals(5, result.size());
            assertEquals(100L, result.totalElements());
            assertEquals(20, result.totalPages());
        }
    }

    // ─── TryGetByService ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("TryGetByService")
    class TryGetByServiceTest {

        @Test
        @DisplayName("Deve retornar a entidade quando Optional está presente")
        void execute_encontrado_retornaEntidade() {
            var result = TryGetByService.execute(
                    () -> Optional.of("entidade"),
                    UserNotFoundException::new,
                    log
            );

            assertEquals("entidade", result);
        }

        @Test
        @DisplayName("Deve lançar a exceção fornecida quando Optional está vazio")
        void execute_naoEncontrado_lancaExcecaoFornecida() {
            assertThrows(UserNotFoundException.class,
                    () -> TryGetByService.execute(
                            Optional::empty,
                            () -> new UserNotFoundException(),
                            log
                    ));
        }

        @Test
        @DisplayName("Deve relançar a mesma exceção de domínio sem encapsular")
        void execute_excecaoDominio_relancastMesmaExcecao() {
            assertThrows(UserNotFoundException.class,
                    () -> TryGetByService.execute(
                            () -> { throw new UserNotFoundException(); },
                            () -> new UserNotFoundException(),
                            log
                    ));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException para exceção de infraestrutura diferente da esperada")
        void execute_excecaoInfra_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> TryGetByService.execute(
                            () -> { throw new RuntimeException("Connection refused"); },
                            () -> new UserNotFoundException(),
                            log
                    ));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException para exceção de infraestrutura com tipo diferente")
        void execute_tiposExcecaoDiferentes_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> TryGetByService.execute(
                            () -> { throw new InvalidDataException("bad data"); },
                            () -> new UserNotFoundException(),
                            log
                    ));
        }

        @Test
        @DisplayName("Deve funcionar com diferentes tipos de entidade")
        void execute_tiposGenericos() {
            var resultado = TryGetByService.execute(
                    () -> Optional.of(42),
                    () -> new InvalidDataException("not found"),
                    log
            );

            assertEquals(42, resultado);
        }
    }

    // ─── TrySaveService ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("TrySaveService")
    class TrySaveServiceTest {

        @Test
        @DisplayName("Deve executar saveAction com a entidade fornecida")
        void execute_salvaSucesso() {
            var salvo = new AtomicBoolean(false);

            assertDoesNotThrow(() -> TrySaveService.execute(
                    "entidade",
                    e -> salvo.set(true),
                    log
            ));

            assertTrue(salvo.get());
        }

        @Test
        @DisplayName("Deve passar a entidade correta para o saveAction")
        void execute_passaEntidadeCorreta() {
            var capturado = new java.util.concurrent.atomic.AtomicReference<String>();

            TrySaveService.execute("minha-entidade", capturado::set, log);

            assertEquals("minha-entidade", capturado.get());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException quando saveAction falha")
        void execute_saveActionFalha_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> TrySaveService.execute(
                            "entidade",
                            e -> { throw new RuntimeException("DB write failed"); },
                            log
                    ));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException para qualquer tipo de Exception no save")
        void execute_checkedException_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> TrySaveService.execute(
                            "entidade",
                            e -> { throw new RuntimeException(new Exception("IO error")); },
                            log
                    ));
        }

        @Test
        @DisplayName("Deve funcionar com diferentes tipos de entidade")
        void execute_tiposGenericos() {
            var salvo = new AtomicBoolean(false);

            assertDoesNotThrow(() -> TrySaveService.execute(
                    42,
                    e -> salvo.set(true),
                    log
            ));

            assertTrue(salvo.get());
        }
    }

    // ─── VerifyIfExistsModifyAndSaveService ───────────────────────────────────

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveService")
    class VerifyIfExistsModifyAndSaveServiceTest {

        @Test
        @DisplayName("Deve buscar, modificar e salvar a entidade em sequência")
        void execute_fluxoCompleto_sucesso() {
            var entidade = new StringBuilder("original");
            var salvo = new AtomicBoolean(false);

            VerifyIfExistsModifyAndSaveService.execute(
                    () -> Optional.of(entidade),
                    e -> e.replace(0, e.length(), "modificado"),
                    () -> new UserNotFoundException(),
                    e -> salvo.set(true),
                    log
            );

            assertEquals("modificado", entidade.toString());
            assertTrue(salvo.get());
        }

        @Test
        @DisplayName("Deve lançar exceção de domínio sem salvar quando entidade não existe")
        void execute_naoEncontrado_naoSalva() {
            var salvo = new AtomicBoolean(false);

            assertThrows(UserNotFoundException.class,
                    () -> VerifyIfExistsModifyAndSaveService.execute(
                            Optional::empty,
                            e -> {},
                            () -> new UserNotFoundException(),
                            e -> salvo.set(true),
                            log
                    ));

            assertFalse(salvo.get());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException quando modify falha")
        void execute_modifyFalha_throwsProcessing() {
            var salvo = new AtomicBoolean(false);

            assertThrows(ProcessingErrorException.class,
                    () -> VerifyIfExistsModifyAndSaveService.execute(
                            () -> Optional.of("entidade"),
                            e -> { throw new RuntimeException("modify error"); },
                            () -> new UserNotFoundException(),
                            e -> salvo.set(true),
                            log
                    ));

            assertFalse(salvo.get());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException quando save falha")
        void execute_saveFalha_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> VerifyIfExistsModifyAndSaveService.execute(
                            () -> Optional.of("entidade"),
                            e -> {},
                            () -> new UserNotFoundException(),
                            e -> { throw new RuntimeException("DB error"); },
                            log
                    ));
        }

        @Test
        @DisplayName("Deve aplicar a modificação antes de salvar")
        void execute_ordemModificarAntesDeSalvar() {
            var log2 = LoggerFactory.getLogger("test");
            var ordem = new java.util.ArrayList<String>();

            VerifyIfExistsModifyAndSaveService.execute(
                    () -> Optional.of("entidade"),
                    e -> ordem.add("modify"),
                    () -> new UserNotFoundException(),
                    e -> ordem.add("save"),
                    log2
            );

            assertEquals(List.of("modify", "save"), ordem);
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException para exceção de infra no get")
        void execute_getInfraFalha_throwsProcessing() {
            assertThrows(ProcessingErrorException.class,
                    () -> VerifyIfExistsModifyAndSaveService.execute(
                            () -> { throw new RuntimeException("timeout"); },
                            e -> {},
                            () -> new UserNotFoundException(),
                            e -> {},
                            log
                    ));
        }
    }
}