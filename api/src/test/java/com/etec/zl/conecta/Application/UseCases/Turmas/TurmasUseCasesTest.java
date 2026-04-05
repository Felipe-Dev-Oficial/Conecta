package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;
import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.Status;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Turmas UseCases")
class TurmasUseCasesTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Turma buildTurma(Cursos curso, Status status) {
        return new Turma(UUID.randomUUID(), curso, 3, 1, status);
    }

    private PageResult<Turma> pageOf(Turma... turmas) {
        return new PageResult<>(List.of(turmas), 0, 10, turmas.length, 1);
    }

    // ─── ListarTodasAsTurmasUseCase ───────────────────────────────────────────

    @Nested
    @DisplayName("ListarTodasAsTurmasUseCase")
    class ListarTodasTest {

        @Mock TurmaRepository repository;
        ListarTodasAsTurmasUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ListarTodasAsTurmasUseCase(repository); }

        @Test
        @DisplayName("Deve retornar todas as turmas paginadas")
        void findAllTurmas_sucesso() {
            var req = new PageRequest(0, 10);
            var turma = buildTurma(Cursos.ADMINISTRACAO, Status.ON);
            when(repository.findAllTurmas(req)).thenReturn(pageOf(turma));

            var result = useCase.findAllTurmas(req);

            assertEquals(1, result.content().size());
            assertEquals(turma, result.content().get(0));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha de infraestrutura")
        void findAllTurmas_falhaInfra() {
            var req = new PageRequest(0, 10);
            when(repository.findAllTurmas(req)).thenThrow(new RuntimeException("DB error"));

            assertThrows(ProcessingErrorException.class, () -> useCase.findAllTurmas(req));
        }
    }

    // ─── ListarTodasAsTurmasAtuaisUseCase ─────────────────────────────────────

    @Nested
    @DisplayName("ListarTodasAsTurmasAtuaisUseCase")
    class ListarTodasAtuaisTest {

        @Mock TurmaRepository repository;
        ListarTodasAsTurmasAtuaisUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ListarTodasAsTurmasAtuaisUseCase(repository); }

        @Test
        @DisplayName("Deve retornar apenas turmas com status ON")
        void findAllTurmasAtuais_sucesso() {
            var req = new PageRequest(0, 10);
            var turmaOn = buildTurma(Cursos.LOGISTICA, Status.ON);
            when(repository.findAllTurmasAtuais(req)).thenReturn(pageOf(turmaOn));

            var result = useCase.findAllTurmasAtuais(req);

            assertEquals(1, result.content().size());
            assertEquals(Status.ON, result.content().get(0).getStatus());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha de infraestrutura")
        void findAllTurmasAtuais_falhaInfra() {
            var req = new PageRequest(0, 10);
            when(repository.findAllTurmasAtuais(req)).thenThrow(new RuntimeException("DB error"));

            assertThrows(ProcessingErrorException.class, () -> useCase.findAllTurmasAtuais(req));
        }
    }

    // ─── ListarTurmasPorCursoUseCase ──────────────────────────────────────────

    @Nested
    @DisplayName("ListarTurmasPorCursoUseCase")
    class ListarPorCursoTest {

        @Mock TurmaRepository repository;
        ListarTurmasPorCursoUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ListarTurmasPorCursoUseCase(repository); }

        @Test
        @DisplayName("Deve retornar turmas filtradas por curso")
        void findAllTurmasByCurso_sucesso() {
            var req = new PageRequest(0, 10);
            var turma = buildTurma(Cursos.ADMINISTRACAO, Status.ON);
            when(repository.findAllTurmasByCurso(Cursos.ADMINISTRACAO, req)).thenReturn(pageOf(turma));

            var result = useCase.findAllTurmasByCurso(Cursos.ADMINISTRACAO, req);

            assertEquals(1, result.content().size());
            assertEquals(Cursos.ADMINISTRACAO, result.content().get(0).getCurso());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha de infraestrutura")
        void findAllTurmasByCurso_falhaInfra() {
            var req = new PageRequest(0, 10);
            when(repository.findAllTurmasByCurso(any(), eq(req)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.findAllTurmasByCurso(Cursos.ADMINISTRACAO, req));
        }
    }

    // ─── ListarTurmasPorCursoAtuaisUseCase ────────────────────────────────────

    @Nested
    @DisplayName("ListarTurmasPorCursoAtuaisUseCase")
    class ListarPorCursoAtuaisTest {

        @Mock TurmaRepository repository;
        ListarTurmasPorCursoAtuaisUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ListarTurmasPorCursoAtuaisUseCase(repository); }

        @Test
        @DisplayName("Deve retornar turmas ativas filtradas por curso")
        void findAllTurmasByCursoAtuais_sucesso() {
            var req = new PageRequest(0, 10);
            var turma = buildTurma(Cursos.LOGISTICA, Status.ON);
            when(repository.findAllTurmasByCursoAtuais(Cursos.LOGISTICA, req)).thenReturn(pageOf(turma));

            var result = useCase.findAllTurmasByCursoAtuais(Cursos.LOGISTICA, req);

            assertEquals(1, result.content().size());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha de infraestrutura")
        void findAllTurmasByCursoAtuais_falhaInfra() {
            var req = new PageRequest(0, 10);
            when(repository.findAllTurmasByCursoAtuais(any(), eq(req)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.findAllTurmasByCursoAtuais(Cursos.LOGISTICA, req));
        }
    }

    // ─── EncontrarTurmaPorIdUseCase ───────────────────────────────────────────

    @Nested
    @DisplayName("EncontrarTurmaPorIdUseCase")
    class EncontrarPorIdTest {

        @Mock TurmaRepository repository;
        EncontrarTurmaPorIdUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new EncontrarTurmaPorIdUseCase(repository); }

        @Test
        @DisplayName("Deve retornar a turma encontrada")
        void findTurmaPorId_sucesso() {
            var id = UUID.randomUUID();
            var turma = buildTurma(Cursos.ADMINISTRACAO, Status.ON);
            when(repository.findById(id)).thenReturn(Optional.of(turma));

            var result = useCase.findTurmaPorId(id);

            assertEquals(turma, result);
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException se turma não existe")
        void findTurmaPorId_naoEncontrada() {
            var id = UUID.randomUUID();
            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(InvalidDataException.class, () -> useCase.findTurmaPorId(id));
        }
    }

    // ─── NovasTurmasUseCase ───────────────────────────────────────────────────

    @Nested
    @DisplayName("NovasTurmasUseCase")
    class NovasTurmasTest {

        @Mock TurmaRepository repository;
        @Mock
        TurmaMapper mapper;
        NovasTurmasUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new NovasTurmasUseCase(repository, mapper); }

        @Test
        @DisplayName("Deve salvar todas as turmas da lista")
        void cadastroTurmas_sucesso() {
            var dto1 = new DTOCadastroTurma(Cursos.ADMINISTRACAO, 3);
            var dto2 = new DTOCadastroTurma(Cursos.LOGISTICA, 4);
            var turma1 = buildTurma(Cursos.ADMINISTRACAO, Status.ON);
            var turma2 = buildTurma(Cursos.LOGISTICA, Status.ON);

            when(mapper.toRegister(dto1)).thenReturn(turma1);
            when(mapper.toRegister(dto2)).thenReturn(turma2);

            assertDoesNotThrow(() -> useCase.cadastroTurmas(List.of(dto1, dto2)));

            verify(repository).save(turma1);
            verify(repository).save(turma2);
        }

        @Test
        @DisplayName("Deve salvar nenhuma turma para lista vazia")
        void cadastroTurmas_listaVazia() {
            assertDoesNotThrow(() -> useCase.cadastroTurmas(List.of()));
            verify(repository, never()).save(any());
        }
    }

    // ─── PassaModuloUseCase ───────────────────────────────────────────────────

    @Nested
    @DisplayName("PassaModuloUseCase")
    class PassaModuloTest {

        @Mock TurmaRepository repository;
        PassaModuloUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new PassaModuloUseCase(repository); }

        @Test
        @DisplayName("Deve chamar repository.passaModulo()")
        void passaModulo_sucesso() {
            assertDoesNotThrow(() -> useCase.passaModulo());
            verify(repository).passaModulo();
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha de infraestrutura")
        void passaModulo_falhaInfra() {
            doThrow(new RuntimeException("DB error")).when(repository).passaModulo();

            assertThrows(ProcessingErrorException.class, () -> useCase.passaModulo());
        }
    }
}