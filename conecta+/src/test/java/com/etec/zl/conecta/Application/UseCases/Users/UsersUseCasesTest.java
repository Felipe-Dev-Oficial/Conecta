package com.etec.zl.conecta.Application.UseCases.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Application.Services.Services.Users.*;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.Exceptions.ValidationFailedException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Users UseCases")
class UsersUseCasesTest {

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(String id, Tipo tipo) {
        return new User(id, new Name("João Silva"), new Email("joao@etec.com"),
                new PhoneNumber("11987654321"), new Password("Etec@1234"), tipo, new ArrayList<>());
    }

    private DTORetornoNormal dtoNormal(User user) {
        return new DTORetornoNormal(user.getId(), user.getNome(), user.getTipo());
    }

    private DTORetornoSecretaria dtoSecretaria(User user) {
        return new DTORetornoSecretaria(user.getId(), user.getNome(),
                user.getEmail(), user.getNumero(), user.getTipo());
    }

    // ─── Listagens Professor ──────────────────────────────────────────────────

    @Nested
    @DisplayName("ProfessorListagemUseCase")
    class ProfessorListagemTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersService service;
        ProfessorListagemUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ProfessorListagemUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar alunos do professor paginados")
        void findAllAlunos_sucesso() {
            var user = buildUser("a1", Tipo.ALUNO);
            var dto = dtoNormal(user);
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.findAllAlunos("p1", req);

            assertEquals(1, result.content().size());
            verify(service).execute(any(), any());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException em falha de infraestrutura")
        void findAllAlunos_falhaInfra() {
            var req = new PageRequest(0, 10);
            when(service.execute(any(), any())).thenThrow(new ProcessingErrorException());

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.findAllAlunos("p1", req));
        }
    }

    @Nested
    @DisplayName("ProfessorListagemPorTurmaUseCase")
    class ProfessorListagemPorTurmaTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersService service;
        ProfessorListagemPorTurmaUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ProfessorListagemPorTurmaUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar alunos da turma do professor")
        void professorListagemPorTurma_sucesso() {
            var idTurma = UUID.randomUUID();
            var dto = dtoNormal(buildUser("a1", Tipo.ALUNO));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.professorListagemPorTurma("p1", idTurma, req);

            assertEquals(1, result.content().size());
            verify(service).execute(any(), any());
        }
    }

    @Nested
    @DisplayName("ProfessorListagemPorNomeUseCase")
    class ProfessorListagemPorNomeTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersService service;
        ProfessorListagemPorNomeUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ProfessorListagemPorNomeUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar alunos filtrados por nome")
        void professorListagemPorNome_sucesso() {
            var nome = new Name("João Silva");
            var dto = dtoNormal(buildUser("a1", Tipo.ALUNO));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.professorListagemPorNome("p1", nome, req);

            assertEquals(1, result.content().size());
        }
    }

//    @Nested
//    @DisplayName("ProfessorBuscaPorIdUseCase")
//    class ProfessorBuscaPorIdTest {
//
//        @Mock UserRepository repository;
//        @Mock TryGetByUserService service;
//        ProfessorBuscaPorIdUseCase useCase;
//
//        @BeforeEach
//        void setUp() { useCase = new ProfessorBuscaPorIdUseCase(repository, service); }
//
//        @Test
//        @DisplayName("Deve retornar aluno se professor tiver acesso")
//        void professorBuscaPorId_sucesso() {
//            var user = buildUser("a1", Tipo.ALUNO);
//            var dto = dtoNormal(user);
//
//            when(service.execute(any(), any())).thenReturn(dto);
//
//            var result = useCase.professorBuscaPorId("p1", "a1");
//
//            assertEquals(dto, result);
//        }
//
//        @Test
//        @DisplayName("Deve lançar UserNotFoundException se aluno não pertence ao professor")
//        void professorBuscaPorId_naoEncontrado() {
//            when(service.execute(any(), any())).thenThrow(new UserNotFoundException());
//
//            assertThrows(UserNotFoundException.class,
//                    () -> useCase.professorBuscaPorId("p1", "a-inexistente"));
//        }
//    }

    // ─── Listagens Aluno ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("AlunoListagemProfessorUseCase")
    class AlunoListagemProfessorTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersService service;
        AlunoListagemProfessorUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new AlunoListagemProfessorUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar professores do aluno paginados")
        void alunoListagemProfessor_sucesso() {
            var dto = dtoNormal(buildUser("p1", Tipo.PROFESSOR));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.alunoListagemProfessor("a1", req);

            assertEquals(1, result.content().size());
        }
    }

//    @Nested
//    @DisplayName("AlunoBuscaProfessorPorIdUseCase")
//    class AlunoBuscaProfessorPorIdTest {
//
//        @Mock UserRepository repository;
//        @Mock TryGetByUserService service;
//        AlunoBuscaProfessorPorIdUseCase useCase;
//
//        @BeforeEach
//        void setUp() { useCase = new AlunoBuscaProfessorPorIdUseCase(repository, service); }
//
//        @Test
//        @DisplayName("Deve retornar professor se aluno tiver acesso")
//        void alunoBuscaProfessorPorId_sucesso() {
//            var dto = dtoNormal(buildUser("p1", Tipo.PROFESSOR));
//            when(service.execute(any(), any())).thenReturn(dto);
//
//            var result = useCase.alunoBuscaProfessorPorId("a1", "p1");
//
//            assertEquals(dto, result);
//        }
//
//        @Test
//        @DisplayName("Deve lançar UserNotFoundException se professor não pertence ao aluno")
//        void alunoBuscaProfessorPorId_naoEncontrado() {
//            when(service.execute(any(), any())).thenThrow(new UserNotFoundException());
//
//            assertThrows(UserNotFoundException.class,
//                    () -> useCase.alunoBuscaProfessorPorId("a1", "p-inexistente"));
//        }
//    }

    // ─── Listagens Secretaria ─────────────────────────────────────────────────

    @Nested
    @DisplayName("SecretariaListagemUseCase")
    class SecretariaListagemTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersSecretariaService service;
        SecretariaListagemUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SecretariaListagemUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar todos os usuários paginados")
        void retornoTodos_sucesso() {
            var dto = dtoSecretaria(buildUser("u1", Tipo.ALUNO));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.retornoTodos(req);

            assertEquals(1, result.content().size());
        }
    }

    @Nested
    @DisplayName("SecretariaListagemPorCursantesUseCase")
    class SecretariaListagemPorCursantesTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersSecretariaService service;
        SecretariaListagemPorCursantesUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SecretariaListagemPorCursantesUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar apenas alunos cursantes ativos")
        void secretariaListagemPorCursantes_sucesso() {
            var dto = dtoSecretaria(buildUser("a1", Tipo.ALUNO));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.secretariaListagemPorCursantes(req);

            assertEquals(1, result.content().size());
        }
    }

    @Nested
    @DisplayName("SecretariaListagemPorFuncionariosUseCase")
    class SecretariaListagemPorFuncionariosTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersSecretariaService service;
        SecretariaListagemPorFuncionariosUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SecretariaListagemPorFuncionariosUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar professores e secretaria")
        void secretariaListagemPorFuncionarios_sucesso() {
            var dto = dtoSecretaria(buildUser("p1", Tipo.PROFESSOR));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.secretariaListagemPorFuncionarios(req);

            assertEquals(1, result.content().size());
        }
    }

    @Nested
    @DisplayName("SecretariaListagemPorNomeUseCase")
    class SecretariaListagemPorNomeTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersSecretariaService service;
        SecretariaListagemPorNomeUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SecretariaListagemPorNomeUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar usuários filtrados por nome")
        void secretariaListagemPorNome_sucesso() {
            var nome = new Name("João Silva");
            var dto = dtoSecretaria(buildUser("u1", Tipo.ALUNO));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.secretariaListagemPorFuncionarios(nome, req);

            assertEquals(1, result.content().size());
        }
    }

    @Nested
    @DisplayName("SecretariaListagemPorTurmaUseCase")
    class SecretariaListagemPorTurmaTest {

        @Mock UserRepository repository;
        @Mock TryGetUsersSecretariaService service;
        SecretariaListagemPorTurmaUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SecretariaListagemPorTurmaUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar usuários da turma")
        void secretariaListagemPorTurma_sucesso() {
            var idTurma = UUID.randomUUID();
            var dto = dtoSecretaria(buildUser("u1", Tipo.ALUNO));
            var page = new PageResult<>(List.of(dto), 0, 10, 1L, 1);
            var req = new PageRequest(0, 10);

            when(service.execute(any(), any())).thenReturn(page);

            var result = useCase.secretariaListagemPorTurma(idTurma, req);

            assertEquals(1, result.content().size());
        }
    }

    @Nested
    @DisplayName("SecretariaBuscaPorIdUseCase")
    class SecretariaBuscaPorIdTest {

        @Mock UserRepository repository;
        @Mock TryGetByUserSecretariaService service;
        SecretariaBuscaPorIdUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SecretariaBuscaPorIdUseCase(repository, service); }

        @Test
        @DisplayName("Deve retornar usuário completo para secretaria")
        void secretariaBuscaPorId_sucesso() {
            var user = buildUser("u1", Tipo.ALUNO);
            var dto = dtoSecretaria(user);
            when(service.execute(any(), any())).thenReturn(dto);

            var result = useCase.secretariaBuscaPorId("u1");

            assertEquals(dto, result);
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se usuário não existe")
        void secretariaBuscaPorId_naoEncontrado() {
            when(service.execute(any(), any())).thenThrow(new UserNotFoundException());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.secretariaBuscaPorId("inexistente"));
        }
    }

    // ─── Cadastro e Deleção ───────────────────────────────────────────────────

    @Nested
    @DisplayName("SalvarUsuarioUseCase")
    class SalvarUsuarioTest {

        @Mock UserRepository repository;
        @Mock TrySaveUserService saveService;
        @Mock TryGetByUserService getService;
        SalvarUsuarioUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SalvarUsuarioUseCase(saveService, repository, getService); }

        @Test
        @DisplayName("Deve criar novo usuário quando email não existe")
        void salvarUsuario_emailNaoExiste_criaNovo() {
            var user = buildUser("u1", Tipo.ALUNO);
            var dto = new DTOCadastro(user.getId(), user.getNome(), user.getEmail(),
                    user.getNumero(), user.getSenha(), Tipo.ALUNO, new ArrayList<>());

            when(repository.findByEmail(dto.email())).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> useCase.salvarUsuario(dto));
            verify(saveService).execute(dto, any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve adicionar turmas ao usuário existente quando email já existe")
        void salvarUsuario_emailJaExiste_adicionaTurmas() {
            var idTurma = UUID.randomUUID();
            var user = buildUser("u1", Tipo.ALUNO);
            var dto = new DTOCadastro(user.getId(), user.getNome(), user.getEmail(),
                    user.getNumero(), user.getSenha(), Tipo.ALUNO, List.of(idTurma));

            when(repository.findByEmail(dto.email())).thenReturn(Optional.of(user));

            assertDoesNotThrow(() -> useCase.salvarUsuario(dto));
            assertTrue(user.getTurmasIds().contains(idTurma));
            verify(repository).save(user);
            verify(saveService, never()).execute(any(), any());
        }
    }

    @Nested
    @DisplayName("NovosUsuariosUseCase")
    class NovosUsuariosTest {

        @Mock TrySaveUserService service;
        NovosUsuariosUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new NovosUsuariosUseCase(service); }

        @Test
        @DisplayName("Deve salvar todos os usuários da lista")
        void novosUsuarios_sucesso() {
            var user = buildUser("u1", Tipo.ALUNO);
            var dto = new DTOCadastro(user.getId(), user.getNome(), user.getEmail(),
                    user.getNumero(), user.getSenha(), Tipo.ALUNO, new ArrayList<>());

            assertDoesNotThrow(() -> useCase.novosUsuarios(List.of(dto)));
            verify(service).execute(eq(dto), any());
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException se algum usuário falhar")
        void novosUsuarios_falhaEmUm_throwsProcessingError() {
            var user = buildUser("u1", Tipo.ALUNO);
            var dto = new DTOCadastro(user.getId(), user.getNome(), user.getEmail(),
                    user.getNumero(), user.getSenha(), Tipo.ALUNO, new ArrayList<>());

            doThrow(new RuntimeException("DB error")).when(service).execute(any(), any());

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.novosUsuarios(List.of(dto)));
        }

        @Test
        @DisplayName("Não deve salvar nada para lista vazia")
        void novosUsuarios_listaVazia() {
            assertDoesNotThrow(() -> useCase.novosUsuarios(List.of()));
            verify(service, never()).execute(any(), any());
        }
    }

//    @Nested
//    @DisplayName("DeletarUsuarioUseCase")
//    class DeletarUsuarioTest {
//
//        @Mock UserRepository repository;
//        DeletarUsuarioUseCase useCase;
//
//        @BeforeEach
//        void setUp() { useCase = new DeletarUsuarioUseCase(repository); }
//
//        @Test
//        @DisplayName("Deve chamar repository.delete() com o id correto")
//        void deletarUsuario_sucesso() {
//            assertDoesNotThrow(() -> useCase.deletarUsuario("u1"));
//            verify(repository).delete("u1");
//        }
//    }

    // ─── Alterações de dados ──────────────────────────────────────────────────

    @Nested
    @DisplayName("AlterarTipoUseCase")
    class AlterarTipoTest {

        @Mock UserRepository repository;
        @Mock VerifyIfExistsModifyAndSaveUsersService service;
        AlterarTipoUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new AlterarTipoUseCase(repository, service); }

        @Test
        @DisplayName("Deve chamar service com u -> u.alteraTipo(tipo)")
        void alterarTipo_chamaService() {
            assertDoesNotThrow(() -> useCase.alterarTipo("u1", Tipo.PROFESSOR));
            verify(service).execute(eq("u1"), any(), any());
        }
    }

//    @Nested
//    @DisplayName("AlterarEmailUseCase")
//    class AlterarEmailTest {
//
//        @Mock UserRepository repository;
//        @Mock VerifyIfExistsModifyAndSaveUsersService service;
//        AlterarEmailUseCase useCase;
//
//        @BeforeEach
//        void setUp() { useCase = new AlterarEmailUseCase(repository, service); }
//
//        @Test
//        @DisplayName("Deve chamar service com u -> u.checkAndChangeEmail()")
//        void alterarEmail_chamaService() {
//            var token = UUID.randomUUID();
//            var email = new Email("novo@etec.com");
//
//            assertDoesNotThrow(() -> useCase.alterarEmail("u1", token, email));
//            verify(service).execute(eq("u1"), any(), any());
//        }
//    }
//
//    @Nested
//    @DisplayName("AlterarSenhaUseCase")
//    class AlterarSenhaTest {
//
//        @Mock UserRepository repository;
//        @Mock VerifyIfExistsModifyAndSaveUsersService service;
//        AlterarSenhaUseCase useCase;
//
//        @BeforeEach
//        void setUp() { useCase = new AlterarSenhaUseCase(repository, service); }
//
//        @Test
//        @DisplayName("Deve chamar service com u -> u.checkAndChangePassword()")
//        void alterarSenha_chamaService() {
//            var token = UUID.randomUUID();
//            var senha = new Password("NovaSenha@1234");
//
//            assertDoesNotThrow(() -> useCase.alterarSenha("u1", token, senha));
//            verify(service).execute(eq("u1"), any(), any());
//        }
//    }

    // ─── Solicitações de alteração ────────────────────────────────────────────

    @Nested
    @DisplayName("SolicitarAlteracaoEmailUseCase")
    class SolicitarAlteracaoEmailTest {

        @Mock UserRepository repository;
        @Mock StartChangeService service;
        SolicitarAlteracaoEmailUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SolicitarAlteracaoEmailUseCase(repository, service); }

        @Test
        @DisplayName("Deve chamar StartChangeService com User::sendUpdateEmailToken")
        void solicitarAlteracaoEmail_chamaService() {
            assertDoesNotThrow(() -> useCase.solicitarAlteracaoEmail("u1"));
            verify(service).execute(eq("u1"), any(), any(), eq("Alteração de senha"), any());
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se usuário não existe")
        void solicitarAlteracaoEmail_usuarioNaoEncontrado() {
            doThrow(new UserNotFoundException()).when(service)
                    .execute(eq("inexistente"), any(), any(), any(), any());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.solicitarAlteracaoEmail("inexistente"));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException se email falhar")
        void solicitarAlteracaoEmail_emailFalha() {
            doThrow(new ProcessingErrorException()).when(service)
                    .execute(any(), any(), any(), any(), any());

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.solicitarAlteracaoEmail("u1"));
        }
    }

    @Nested
    @DisplayName("SolicitarAlteracaoSenhaUseCase")
    class SolicitarAlteracaoSenhaTest {

        @Mock UserRepository repository;
        @Mock StartChangeService service;
        SolicitarAlteracaoSenhaUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new SolicitarAlteracaoSenhaUseCase(repository, service); }

        @Test
        @DisplayName("Deve chamar StartChangeService com User::sendUpdatePasswordToken")
        void solicitarAlteracaoSenha_chamaService() {
            assertDoesNotThrow(() -> useCase.solicitarAlteracaoSenha("u1"));
            verify(service).execute(eq("u1"), any(), any(), eq("Alteração de senha"), any());
        }

        @Test
        @DisplayName("Deve lançar UserNotFoundException se usuário não existe")
        void solicitarAlteracaoSenha_usuarioNaoEncontrado() {
            doThrow(new UserNotFoundException()).when(service)
                    .execute(eq("inexistente"), any(), any(), any(), any());

            assertThrows(UserNotFoundException.class,
                    () -> useCase.solicitarAlteracaoSenha("inexistente"));
        }

        @Test
        @DisplayName("Deve lançar ProcessingErrorException se email falhar")
        void solicitarAlteracaoSenha_emailFalha() {
            doThrow(new ProcessingErrorException()).when(service)
                    .execute(any(), any(), any(), any(), any());

            assertThrows(ProcessingErrorException.class,
                    () -> useCase.solicitarAlteracaoSenha("u1"));
        }
    }
}