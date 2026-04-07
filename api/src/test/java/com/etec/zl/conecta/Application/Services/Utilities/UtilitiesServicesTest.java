package com.etec.zl.conecta.Application.Services.Utilities;

import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Utilities Services")
class UtilitiesServicesTest {

    private final Logger log = mock(Logger.class);

    // ══════════════════════════════════════════════════════════════════════════
    // TryGetByService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("TryGetByService")
    class TryGetByServiceTests {

        @Nested
        @DisplayName("quando entidade é encontrada")
        class Sucesso {

            @Test
            @DisplayName("deve retornar exatamente a entidade presente no Optional")
            void retornaEntidade() {
                var result = TryGetByService.execute(
                        () -> Optional.of("entidade"),
                        UserNotFoundException::new,
                        log
                );
                assertEquals("entidade", result);
            }

            @Test
            @DisplayName("não deve logar nada em caso de sucesso")
            void naoLoga() {
                TryGetByService.execute(
                        () -> Optional.of("ok"),
                        UserNotFoundException::new,
                        log
                );
                verifyNoInteractions(log);
            }
        }

        @Nested
        @DisplayName("quando entidade não é encontrada")
        class NaoEncontrado {

            @Test
            @DisplayName("deve lançar a exceção fornecida pelo supplier")
            void lancaExcecaoDoSupplier() {
                assertThrows(UserNotFoundException.class, () ->
                        TryGetByService.execute(
                                Optional::empty,
                                UserNotFoundException::new,
                                log
                        )
                );
            }

            @Test
            @DisplayName("deve logar warn com a mensagem da exceção de negócio")
            void logaWarn() {
                try {
                    TryGetByService.execute(Optional::empty, UserNotFoundException::new, log);
                } catch (UserNotFoundException ignored) {}

                verify(log, times(1)).warn(anyString(), any(UserNotFoundException.class));
            }
        }

        @Nested
        @DisplayName("quando o repositório lança RuntimeException inesperada")
        class FalhaInfra {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                assertThrows(ProcessingErrorException.class, () ->
                        TryGetByService.execute(
                                () -> { throw new RuntimeException("DB down"); },
                                UserNotFoundException::new,
                                log
                        )
                );
            }

            @Test
            @DisplayName("deve logar error com a mensagem original da exceção de infra")
            void logaError() {
                try {
                    TryGetByService.execute(
                            () -> { throw new RuntimeException("DB down"); },
                            UserNotFoundException::new,
                            log
                    );
                } catch (ProcessingErrorException ignored) {}

                verify(log, times(1)).error(anyString(), anyString(), any(RuntimeException.class));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TryGetService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("TryGetService")
    class TryGetServiceTests {

        private PageResult<String> pageResultPadrao() {
            return new PageResult<>(List.of("a", "b"), 0, 2, 2L, 1);
        }

        @Nested
        @DisplayName("quando a consulta tem sucesso")
        class Sucesso {

            @Test
            @DisplayName("deve retornar exatamente o PageResult retornado pelo supplier")
            void retornaPageResult() {
                var expected = pageResultPadrao();
                var result = TryGetService.execute(() -> expected, log);
                assertEquals(expected, result);
            }

            @Test
            @DisplayName("deve preservar content, page, size, totalElements e totalPages")
            void preservaCampos() {
                var result = TryGetService.execute(() -> pageResultPadrao(), log);
                assertEquals(List.of("a", "b"), result.content());
                assertEquals(0, result.page());
                assertEquals(2, result.size());
                assertEquals(2L, result.totalElements());
                assertEquals(1, result.totalPages());
            }

            @Test
            @DisplayName("não deve logar nada em caso de sucesso")
            void naoLoga() {
                TryGetService.execute(() -> pageResultPadrao(), log);
                verifyNoInteractions(log);
            }
        }

        @Nested
        @DisplayName("quando o supplier lança exceção")
        class Falha {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                assertThrows(ProcessingErrorException.class, () ->
                        TryGetService.execute(
                                () -> { throw new RuntimeException("DB down"); },
                                log
                        )
                );
            }

            @Test
            @DisplayName("deve logar error com mensagem e causa original")
            void logaError() {
                try {
                    TryGetService.execute(() -> { throw new RuntimeException("DB down"); }, log);
                } catch (ProcessingErrorException ignored) {}

                verify(log, times(1)).error(anyString(), any(Exception.class));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // TrySaveService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("TrySaveService")
    class TrySaveServiceTests {

        @Nested
        @DisplayName("quando o save tem sucesso")
        class Sucesso {

            @Test
            @DisplayName("deve invocar o saveAction exatamente uma vez com a entidade correta")
            void invocaSaveAction() {
                Consumer<String> saveAction = mock();
                TrySaveService.execute("entidade", saveAction, log);
                verify(saveAction, times(1)).accept("entidade");
            }

            @Test
            @DisplayName("não deve logar nada em caso de sucesso")
            void naoLoga() {
                TrySaveService.execute("ok", e -> {}, log);
                verifyNoInteractions(log);
            }
        }

        @Nested
        @DisplayName("quando o saveAction lança exceção")
        class Falha {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                Consumer<String> saveAction = e -> { throw new RuntimeException("DB down"); };
                assertThrows(ProcessingErrorException.class, () ->
                        TrySaveService.execute("entidade", saveAction, log)
                );
            }

            @Test
            @DisplayName("deve logar error com mensagem e causa original")
            void logaError() {
                Consumer<String> saveAction = e -> { throw new RuntimeException("falha"); };
                try {
                    TrySaveService.execute("entidade", saveAction, log);
                } catch (ProcessingErrorException ignored) {}

                verify(log, times(1)).error(anyString(), any(Exception.class));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // VerifyIfExistsModifyAndSaveService
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("VerifyIfExistsModifyAndSaveService")
    class VerifyIfExistsModifyAndSaveServiceTests {

        @Nested
        @DisplayName("quando entidade existe")
        class Sucesso {

            @Test
            @DisplayName("deve invocar modifyMethod exatamente uma vez com a entidade encontrada")
            void invocaModify() {
                Consumer<String> modify = mock();
                Consumer<String> save = mock();

                VerifyIfExistsModifyAndSaveService.execute(
                        () -> Optional.of("entidade"),
                        modify,
                        UserNotFoundException::new,
                        save,
                        log
                );

                verify(modify, times(1)).accept("entidade");
            }

            @Test
            @DisplayName("deve invocar saveAction exatamente uma vez após o modify")
            void invocaSaveAposModify() {
                Consumer<String> modify = mock();
                Consumer<String> save = mock();

                VerifyIfExistsModifyAndSaveService.execute(
                        () -> Optional.of("entidade"),
                        modify,
                        UserNotFoundException::new,
                        save,
                        log
                );

                verify(save, times(1)).accept("entidade");
            }

            @Test
            @DisplayName("modify e save devem ser chamados na ordem correta")
            void ordemModifyDepoisSave() {
                Consumer<String> modify = mock();
                Consumer<String> save = mock();
                var order = inOrder(modify, save);

                VerifyIfExistsModifyAndSaveService.execute(
                        () -> Optional.of("entidade"),
                        modify,
                        UserNotFoundException::new,
                        save,
                        log
                );

                order.verify(modify).accept("entidade");
                order.verify(save).accept("entidade");
            }
        }

        @Nested
        @DisplayName("quando entidade não é encontrada")
        class NaoEncontrado {

            @Test
            @DisplayName("deve lançar a exceção do supplier sem chamar modify nem save")
            void lancaExcecaoSemModificarNemSalvar() {
                Consumer<String> modify = mock();
                Consumer<String> save = mock();

                assertThrows(UserNotFoundException.class, () ->
                        VerifyIfExistsModifyAndSaveService.execute(
                                Optional::empty,
                                modify,
                                UserNotFoundException::new,
                                save,
                                log
                        )
                );

                verifyNoInteractions(modify);
                verifyNoInteractions(save);
            }
        }

        @Nested
        @DisplayName("quando o save falha")
        class FalhaSave {

            @Test
            @DisplayName("deve relançar como ProcessingErrorException")
            void relancaComoProcessingError() {
                assertThrows(ProcessingErrorException.class, () ->
                        VerifyIfExistsModifyAndSaveService.execute(
                                () -> Optional.of("entidade"),
                                e -> {},
                                UserNotFoundException::new,
                                e -> { throw new RuntimeException("DB down"); },
                                log
                        )
                );
            }

            @Test
            @DisplayName("modify deve ter sido chamado mesmo quando o save falha")
            void modifyFoiChamadoMesmoComFalhaSave() {
                Consumer<String> modify = mock();

                try {
                    VerifyIfExistsModifyAndSaveService.execute(
                            () -> Optional.of("entidade"),
                            modify,
                            UserNotFoundException::new,
                            e -> { throw new RuntimeException("DB down"); },
                            log
                    );
                } catch (ProcessingErrorException ignored) {}

                verify(modify, times(1)).accept("entidade");
            }
        }
    }
}