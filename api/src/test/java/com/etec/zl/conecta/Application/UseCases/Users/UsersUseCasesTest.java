package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.StartChangeService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetByUserSecretariaService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersSecretariaService;
import com.etec.zl.conecta.Application.Services.Services.Users.TryGetUsersService;
import com.etec.zl.conecta.Application.Services.Services.Users.TrySaveUserService;
import com.etec.zl.conecta.Application.Services.Services.Users.VerifyIfExistsModifyAndSaveUsersService;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Use Cases")
class UsersUseCasesTest {

    // ── helpers comuns ────────────────────────────────────────────────────────

    private User userPadrao(String id) {
        return new User(
                id,
                new Name("João Silva"),
                new Email("joao@etec.com"),
                new PhoneNumber("11987654321"),
                new Password("SenhaSegura123!"),
                Tipo.ALUNO,
                new ArrayList<>()
        );
    }

    private PageResult<DTORetornoNormal> pageNormal() {
        return new PageResult<>(List.of(), 0, 10, 0L, 0);
    }

    private PageResult<DTORetornoSecretaria> pageSecretaria() {
        return new PageResult<>(List.of(), 0, 10, 0L, 0);
    }

    private PageRequest pageable() {
        return new PageRequest(0, 10);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AlterarEmailUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AlterarEmailUseCase")
    class AlterarEmailUseCaseTests {

        private VerifyIfExistsModifyAndSaveUsersService service;
        private AlterarEmailUseCase useCase;

        @BeforeEach
        void setUp() {
            service = mock(VerifyIfExistsModifyAndSaveUsersService.class);
            useCase = new AlterarEmailUseCase(service);
        }

        @Test
        @DisplayName("deve delegar ao service com id e lambda corretos")
        void delegaAoService() {
            var token = java.util.UUID.randomUUID();
            var email = new Email("novo@etec.com");

            useCase.alterarEmail("user-1", token, email);

            verify(service, times(1)).execute(eq("user-1"), any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AlterarSenhaUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AlterarSenhaUseCase")
    class AlterarSenhaUseCaseTests {

        private VerifyIfExistsModifyAndSaveUsersService service;
        private AlterarSenhaUseCase useCase;

        @BeforeEach
        void setUp() {
            service = mock(VerifyIfExistsModifyAndSaveUsersService.class);
            useCase = new AlterarSenhaUseCase(service);
        }

        @Test
        @DisplayName("deve delegar ao service com id e lambda corretos")
        void delegaAoService() {
            var token = java.util.UUID.randomUUID();
            var senha = new Password("SenhaSegura123!");

            useCase.alterarSenha("user-1", token, senha);

            verify(service, times(1)).execute(eq("user-1"), any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AlterarTipoUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AlterarTipoUseCase")
    class AlterarTipoUseCaseTests {

        private UserRepository repository;
        private VerifyIfExistsModifyAndSaveUsersService service;
        private AlterarTipoUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(VerifyIfExistsModifyAndSaveUsersService.class);
            useCase    = new AlterarTipoUseCase(repository, service);
        }

        @Test
        @DisplayName("deve delegar ao service com o id e o tipo informados")
        void delegaAoService() {
            useCase.alterarTipo("user-1", Tipo.PROFESSOR);

            verify(service, times(1)).execute(eq("user-1"), any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AlunoBuscaProfessorPorIdUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AlunoBuscaProfessorPorIdUseCase")
    class AlunoBuscaProfessorPorIdUseCaseTests {

        private UserRepository repository;
        private TryGetByUserService service;
        private UserMapper mapper;
        private AlunoBuscaProfessorPorIdUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetByUserService.class);
            mapper     = mock(UserMapper.class);
            useCase    = new AlunoBuscaProfessorPorIdUseCase(repository, service, mapper);
        }

        @Test
        @DisplayName("deve retornar o DTORetornoNormal mapeado pelo mapper")
        void retornaDTOMapeado() {
            var user = userPadrao("prof-1");
            var dto  = new DTORetornoNormal("prof-1", user.getNome(), Tipo.PROFESSOR);
            when(service.execute(any(), any())).thenReturn(user);
            when(mapper.toDTOReturn(user)).thenReturn(dto);

            var result = useCase.alunoBuscaProfessorPorId("aluno-1", "prof-1");

            assertEquals(dto, result);
        }

        @Test
        @DisplayName("deve consultar o repository com os ids corretos")
        void consultaRepositoryComIdsCorretos() {
            var user = userPadrao("prof-1");
            when(repository.findByIdProfessores("aluno-1", "prof-1")).thenReturn(Optional.of(user));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return user;
            });
            when(mapper.toDTOReturn(user)).thenReturn(mock(DTORetornoNormal.class));

            useCase.alunoBuscaProfessorPorId("aluno-1", "prof-1");

            verify(repository, times(1)).findByIdProfessores("aluno-1", "prof-1");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AlunoListagemProfessorUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AlunoListagemProfessorUseCase")
    class AlunoListagemProfessorUseCaseTests {

        private UserRepository repository;
        private TryGetUsersService service;
        private AlunoListagemProfessorUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersService.class);
            useCase    = new AlunoListagemProfessorUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageNormal();
            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.alunoListagemProfessor("aluno-1", pageable());

            assertEquals(page, result);
        }

        @Test
        @DisplayName("deve consultar o repository com o id do aluno")
        void consultaRepositoryComIdAluno() {
            var p = pageable();
            when(repository.findAllProfessores(eq("aluno-1"), any())).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageNormal();
            });

            useCase.alunoListagemProfessor("aluno-1", p);

            verify(repository, times(1)).findAllProfessores(eq("aluno-1"), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DeletarUsuarioUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DeletarUsuarioUseCase")
    class DeletarUsuarioUseCaseTests {

        private VerifyIfExistsModifyAndSaveUsersService service;
        private DeletarUsuarioUseCase useCase;

        @BeforeEach
        void setUp() {
            service = mock(VerifyIfExistsModifyAndSaveUsersService.class);
            useCase = new DeletarUsuarioUseCase(service);
        }

        @Test
        @DisplayName("deve delegar ao service com o id correto")
        void delegaAoService() {
            useCase.deletarUsuario("user-1");

            verify(service, times(1)).execute(eq("user-1"), any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NovosUsuariosUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("NovosUsuariosUseCase")
    class NovosUsuariosUseCaseTests {

        private TrySaveUserService service;
        private NovosUsuariosUseCase useCase;

        private DTOCadastro dtoPadrao(String id) {
            return new DTOCadastro(id, new Name("Fulano"), new Email("f@etec.com"),
                    new PhoneNumber("11999999999"), new Password("SenhaSegura123!"), Tipo.ALUNO, List.of());
        }

        @BeforeEach
        void setUp() {
            service = mock(TrySaveUserService.class);
            useCase = new NovosUsuariosUseCase(service);
        }

        @Test
        @DisplayName("deve chamar service.execute para cada dto da lista")
        void chamaServiceParaCadaDto() {
            var dtos = List.of(dtoPadrao("u1"), dtoPadrao("u2"), dtoPadrao("u3"));

            useCase.novosUsuarios(dtos);

            verify(service, times(3)).execute(any(), any());
        }

        @Test
        @DisplayName("deve lançar ProcessingErrorException se algum dto falhar")
        void lancaExcecaoComFalha() {
            doThrow(new RuntimeException("falha")).when(service).execute(any(), any());

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.novosUsuarios(List.of(dtoPadrao("u1"))));
        }

        @Test
        @DisplayName("deve lançar ProcessingErrorException apenas ao final, mesmo com falhas parciais")
        void acumulaFalhasELancaNoFinal() {
            var dtos = List.of(dtoPadrao("u1"), dtoPadrao("u2"));
            doThrow(new RuntimeException("falha")).when(service).execute(any(), any());

            assertThrows(ProcessingErrorException.class, () -> useCase.novosUsuarios(dtos));
            // ambos foram tentados mesmo com falha
            verify(service, times(2)).execute(any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ProfessorBuscaPorIdUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ProfessorBuscaPorIdUseCase")
    class ProfessorBuscaPorIdUseCaseTests {

        private UserRepository repository;
        private TryGetByUserService service;
        private UserMapper mapper;
        private ProfessorBuscaPorIdUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetByUserService.class);
            mapper     = mock(UserMapper.class);
            useCase    = new ProfessorBuscaPorIdUseCase(repository, service, mapper);
        }

        @Test
        @DisplayName("deve retornar o DTORetornoNormal mapeado pelo mapper")
        void retornaDTOMapeado() {
            var user = userPadrao("aluno-1");
            var dto  = new DTORetornoNormal("aluno-1", user.getNome(), Tipo.ALUNO);
            when(service.execute(any(), any())).thenReturn(user);
            when(mapper.toDTOReturn(user)).thenReturn(dto);

            var result = useCase.professorBuscaPorId("prof-1", "aluno-1");

            assertEquals(dto, result);
        }

        @Test
        @DisplayName("deve consultar o repository com os ids corretos")
        void consultaRepositoryComIdsCorretos() {
            var user = userPadrao("aluno-1");
            when(repository.findByIdAlunos("prof-1", "aluno-1")).thenReturn(Optional.of(user));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return user;
            });
            when(mapper.toDTOReturn(user)).thenReturn(mock(DTORetornoNormal.class));

            useCase.professorBuscaPorId("prof-1", "aluno-1");

            verify(repository, times(1)).findByIdAlunos("prof-1", "aluno-1");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ProfessorListagemUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ProfessorListagemUseCase")
    class ProfessorListagemUseCaseTests {

        private UserRepository repository;
        private TryGetUsersService service;
        private ProfessorListagemUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersService.class);
            useCase    = new ProfessorListagemUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageNormal();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.findAllAlunos("prof-1", pageable()));
        }

        @Test
        @DisplayName("deve consultar o repository com o id do professor")
        void consultaRepositoryComIdProfessor() {
            when(repository.findAllAlunos(eq("prof-1"), any())).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageNormal();
            });

            useCase.findAllAlunos("prof-1", pageable());

            verify(repository, times(1)).findAllAlunos(eq("prof-1"), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ProfessorListagemPorNomeUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ProfessorListagemPorNomeUseCase")
    class ProfessorListagemPorNomeUseCaseTests {

        private UserRepository repository;
        private TryGetUsersService service;
        private ProfessorListagemPorNomeUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersService.class);
            useCase    = new ProfessorListagemPorNomeUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageNormal();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.professorListagemPorNome("prof-1", new Name("João"), pageable()));
        }

        @Test
        @DisplayName("deve consultar o repository com o id e o nome informados")
        void consultaRepositoryComIdENome() {
            var nome = new Name("João");
            when(repository.findAllAlunosByNome(eq("prof-1"), eq(nome), any())).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageNormal();
            });

            useCase.professorListagemPorNome("prof-1", nome, pageable());

            verify(repository, times(1)).findAllAlunosByNome(eq("prof-1"), eq(nome), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ProfessorListagemPorTurmaUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ProfessorListagemPorTurmaUseCase")
    class ProfessorListagemPorTurmaUseCaseTests {

        private UserRepository repository;
        private TryGetUsersService service;
        private ProfessorListagemPorTurmaUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersService.class);
            useCase    = new ProfessorListagemPorTurmaUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageNormal();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.professorListagemPorTurma("prof-1", "turma-1", pageable()));
        }

        @Test
        @DisplayName("deve consultar o repository com o id do professor e da turma")
        void consultaRepositoryComIdsCorretos() {
            when(repository.findAllAlunosByTurma(eq("prof-1"), eq("turma-1"), any())).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageNormal();
            });

            useCase.professorListagemPorTurma("prof-1", "turma-1", pageable());

            verify(repository, times(1)).findAllAlunosByTurma(eq("prof-1"), eq("turma-1"), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // RetornarSecretariaUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("RetornarSecretariaUseCase")
    class RetornarSecretariaUseCaseTests {

        private UserRepository repository;
        private TryGetUsersService service;
        private RetornarSecretariaUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersService.class);
            useCase    = new RetornarSecretariaUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageNormal();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.listagemSecretaria(pageable()));
        }

        @Test
        @DisplayName("deve consultar o repository com o pageable correto")
        void consultaRepositoryComPageable() {
            var p = pageable();
            when(repository.findAllSecretaria(p)).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageNormal();
            });

            useCase.listagemSecretaria(p);

            verify(repository, times(1)).findAllSecretaria(p);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SalvarUsuarioUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SalvarUsuarioUseCase")
    class SalvarUsuarioUseCaseTests {

        private TrySaveUserService service;
        private UserRepository repository;
        private TryGetByUserService getService;
        private SalvarUsuarioUseCase useCase;

        private DTOCadastro dto;
        private User user;

        @BeforeEach
        void setUp() {
            service    = mock(TrySaveUserService.class);
            repository = mock(UserRepository.class);
            getService = mock(TryGetByUserService.class);
            useCase    = new SalvarUsuarioUseCase(service, repository, getService);

            dto  = new DTOCadastro("user-1", new Name("João"), new Email("joao@etec.com"),
                    new PhoneNumber("11999999999"), new Password("SenhaSegura123!"), Tipo.ALUNO, List.of());
            user = userPadrao("user-1");
        }

        @Test
        @DisplayName("deve chamar service.execute quando usuário não existe ainda")
        void salvaNovoUsuario() {
            when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());

            useCase.salvarUsuario(dto);

            verify(service, times(1)).execute(eq(dto), any());
        }

        @Test
        @DisplayName("deve atualizar turmas e salvar quando usuário já existe com mesmo tipo")
        void atualizaTurmasDeUsuarioExistente() {
            when(repository.findByEmail(dto.email())).thenReturn(Optional.of(user));

            useCase.salvarUsuario(dto);

            verify(repository, times(1)).save(user);
            verify(service, never()).execute(any(), any());
        }

        @Test
        @DisplayName("deve alterar tipo quando usuário existe com tipo diferente")
        void alteraTipoQuandoDiferente() {
            var dtoProf = new DTOCadastro("user-1", new Name("João"), new Email("joao@etec.com"),
                    new PhoneNumber("11999999999"), new Password("SenhaSegura123!"), Tipo.PROFESSOR, List.of());
            // user é ALUNO, dto é PROFESSOR → deve alterar tipo
            when(repository.findByEmail(dtoProf.email())).thenReturn(Optional.of(user));

            useCase.salvarUsuario(dtoProf);

            assertEquals(Tipo.PROFESSOR, user.getTipo());
            verify(repository, times(1)).save(user);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SecretariaBuscaPorIdUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SecretariaBuscaPorIdUseCase")
    class SecretariaBuscaPorIdUseCaseTests {

        private UserRepository repository;
        private TryGetByUserSecretariaService service;
        private SecretariaBuscaPorIdUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetByUserSecretariaService.class);
            useCase    = new SecretariaBuscaPorIdUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o DTORetornoSecretaria devolvido pelo service")
        void retornaDTO() {
            var dto = mock(DTORetornoSecretaria.class);
            when(service.execute(any(), any())).thenReturn(dto);

            assertEquals(dto, useCase.secretariaBuscaPorId("user-1"));
        }

        @Test
        @DisplayName("deve consultar o repository com o id correto")
        void consultaRepositoryComId() {
            var user = userPadrao("user-1");
            when(repository.findById("user-1")).thenReturn(Optional.of(user));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return mock(DTORetornoSecretaria.class);
            });

            useCase.secretariaBuscaPorId("user-1");

            verify(repository, times(1)).findById("user-1");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SecretariaListagemUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SecretariaListagemUseCase")
    class SecretariaListagemUseCaseTests {

        private UserRepository repository;
        private TryGetUsersSecretariaService service;
        private SecretariaListagemUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersSecretariaService.class);
            useCase    = new SecretariaListagemUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageSecretaria();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.retornoTodos(pageable()));
        }

        @Test
        @DisplayName("deve consultar o repository com o pageable correto")
        void consultaRepository() {
            var p = pageable();
            when(repository.findAll(p)).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageSecretaria();
            });

            useCase.retornoTodos(p);

            verify(repository, times(1)).findAll(p);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SecretariaListagemPorCursantesUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SecretariaListagemPorCursantesUseCase")
    class SecretariaListagemPorCursantesUseCaseTests {

        private UserRepository repository;
        private TryGetUsersSecretariaService service;
        private SecretariaListagemPorCursantesUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersSecretariaService.class);
            useCase    = new SecretariaListagemPorCursantesUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageSecretaria();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.secretariaListagemPorCursantes(pageable()));
        }

        @Test
        @DisplayName("deve consultar findAllCursantes no repository")
        void consultaFindAllCursantes() {
            var p = pageable();
            when(repository.findAllCursantes(p)).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageSecretaria();
            });

            useCase.secretariaListagemPorCursantes(p);

            verify(repository, times(1)).findAllCursantes(p);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SecretariaListagemPorFuncionariosUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SecretariaListagemPorFuncionariosUseCase")
    class SecretariaListagemPorFuncionariosUseCaseTests {

        private UserRepository repository;
        private TryGetUsersSecretariaService service;
        private SecretariaListagemPorFuncionariosUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersSecretariaService.class);
            useCase    = new SecretariaListagemPorFuncionariosUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageSecretaria();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.secretariaListagemPorFuncionarios(pageable()));
        }

        @Test
        @DisplayName("deve consultar findAllFuncionarios no repository")
        void consultaFindAllFuncionarios() {
            var p = pageable();
            when(repository.findAllFuncionarios(p)).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageSecretaria();
            });

            useCase.secretariaListagemPorFuncionarios(p);

            verify(repository, times(1)).findAllFuncionarios(p);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SecretariaListagemPorNomeUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SecretariaListagemPorNomeUseCase")
    class SecretariaListagemPorNomeUseCaseTests {

        private UserRepository repository;
        private TryGetUsersSecretariaService service;
        private SecretariaListagemPorNomeUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersSecretariaService.class);
            useCase    = new SecretariaListagemPorNomeUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageSecretaria();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.secretariaListagemPorFuncionarios(new Name("João"), pageable()));
        }

        @Test
        @DisplayName("deve consultar findAllByName com o nome informado")
        void consultaFindAllByName() {
            var nome = new Name("João");
            var p    = pageable();
            when(repository.findAllByName(nome, p)).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageSecretaria();
            });

            useCase.secretariaListagemPorFuncionarios(nome, p);

            verify(repository, times(1)).findAllByName(nome, p);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SecretariaListagemPorTurmaUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SecretariaListagemPorTurmaUseCase")
    class SecretariaListagemPorTurmaUseCaseTests {

        private UserRepository repository;
        private TryGetUsersSecretariaService service;
        private SecretariaListagemPorTurmaUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(TryGetUsersSecretariaService.class);
            useCase    = new SecretariaListagemPorTurmaUseCase(repository, service);
        }

        @Test
        @DisplayName("deve retornar o PageResult devolvido pelo service")
        void retornaPageResult() {
            var page = pageSecretaria();
            when(service.execute(any(), any())).thenReturn(page);

            assertEquals(page, useCase.secretariaListagemPorTurma("turma-1", pageable()));
        }

        @Test
        @DisplayName("deve consultar findAllByTurma com o id da turma")
        void consultaFindAllByTurma() {
            var p = pageable();
            when(repository.findAllByTurma("turma-1", p)).thenReturn(new PageResult<>(List.of(), 0, 10, 0L, 0));
            when(service.execute(any(), any())).thenAnswer(inv -> {
                Supplier<?> supplier = inv.getArgument(0);
                supplier.get();
                return pageSecretaria();
            });

            useCase.secretariaListagemPorTurma("turma-1", p);

            verify(repository, times(1)).findAllByTurma("turma-1", p);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SolicitarAlteracaoEmailUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SolicitarAlteracaoEmailUseCase")
    class SolicitarAlteracaoEmailUseCaseTests {

        private UserRepository repository;
        private StartChangeService service;
        private SolicitarAlteracaoEmailUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(StartChangeService.class);
            useCase    = new SolicitarAlteracaoEmailUseCase(repository, service);
        }

        @Test
        @DisplayName("deve delegar ao service com o id e assunto de alteração de email")
        void delegaAoService() {
            useCase.solicitarAlteracaoEmail("user-1");

            verify(service, times(1)).execute(eq("user-1"), any(), any(), eq("Alteração de email"), any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SolicitarAlteracaoSenhaUseCase
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("SolicitarAlteracaoSenhaUseCase")
    class SolicitarAlteracaoSenhaUseCaseTests {

        private UserRepository repository;
        private StartChangeService service;
        private SolicitarAlteracaoSenhaUseCase useCase;

        @BeforeEach
        void setUp() {
            repository = mock(UserRepository.class);
            service    = mock(StartChangeService.class);
            useCase    = new SolicitarAlteracaoSenhaUseCase(repository, service);
        }

        @Test
        @DisplayName("deve delegar ao service com o id e assunto de alteração de senha")
        void delegaAoService() {
            useCase.solicitarAlteracaoSenha("user-1");

            verify(service, times(1)).execute(eq("user-1"), any(), any(), eq("Alteração de senha"), any());
        }
    }
}