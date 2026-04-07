package com.etec.zl.conecta.Application.UseCases.Turmas;

import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;
import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.Exceptions.InvalidDataException;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Casos de Uso de Turmas")
class TurmasUseCasesTest {

    // ══════════════════════════════════════════════════════════════════════════
    // NovasTurmasUseCase
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("NovasTurmasUseCase")
    class NovasTurmasUseCaseTests {
        private TurmaRepository repository;
        private TurmaMapper mapper;
        private NovasTurmasUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(TurmaRepository.class);
            mapper = mock(TurmaMapper.class);
            useCase = new NovasTurmasUseCase(repository, mapper);
        }

        @Test
        @DisplayName("Deve mapear e salvar cada turma da lista")
        void cadastraListaDeTurmas() {
            var dto = new DTOCadastroTurma(Cursos.DESENVOLVIMENTO_DE_SISTEMAS, 3);
            var turma = mock(Turma.class);

            when(mapper.toRegister(any(DTOCadastroTurma.class))).thenReturn(turma);

            useCase.cadastroTurmas(List.of(dto));

            verify(mapper, times(1)).toRegister(eq(dto));
            verify(repository, times(1)).save(eq(turma));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // EncontrarTurmaPorIdUseCase
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("EncontrarTurmaPorIdUseCase")
    class EncontrarTurmaPorIdTests {
        private TurmaRepository repository;
        private EncontrarTurmaPorIdUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(TurmaRepository.class);
            useCase = new EncontrarTurmaPorIdUseCase(repository);
        }

        @Test
        @DisplayName("Deve retornar a turma quando o ID existir")
        void retornaTurmaExistente() {
            var turma = mock(Turma.class);
            when(repository.findById("DS-N-1")).thenReturn(Optional.of(turma));

            var resultado = useCase.findTurmaPorId("DS-N-1");

            assertNotNull(resultado);
            assertEquals(turma, resultado);
        }

        @Test
        @DisplayName("Deve lançar InvalidDataException quando a turma não existir")
        void erroTurmaInexistente() {
            when(repository.findById("ID-INVALIDO")).thenReturn(Optional.empty());

            assertThrows(InvalidDataException.class, () -> useCase.findTurmaPorId("ID-INVALIDO"));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PassaModuloUseCase
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("PassaModuloUseCase")
    class PassaModuloTests {
        private TurmaRepository repository;
        private PassaModuloUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(TurmaRepository.class);
            useCase = new PassaModuloUseCase(repository);
        }

        @Test
        @DisplayName("Deve chamar o método passaModulo do repositório")
        void chamaPassaModulo() {
            useCase.passaModulo();
            verify(repository, times(1)).passaModulo();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ListarTodasAsTurmasUseCase (Exemplo de Listagem)
    // ══════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ListarTodasAsTurmasUseCase")
    class ListarTurmasTests {
        private TurmaRepository repository;
        private ListarTodasAsTurmasUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(TurmaRepository.class);
            useCase = new ListarTodasAsTurmasUseCase(repository);
        }

        @Test
        @DisplayName("Deve retornar página de turmas através do service utilitário")
        void retornaPaginacao() {
            var pageRequest = new PageRequest(0, 10);
            var expectedResult = new PageResult<Turma>(List.of(), 0, 0, 0, 0);

            when(repository.findAllTurmas(any())).thenReturn(expectedResult);

            var result = useCase.findAllTurmas(pageRequest);

            assertNotNull(result);
            verify(repository, times(1)).findAllTurmas(eq(pageRequest));
        }
    }
}