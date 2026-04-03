package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessageSecretaria;
import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;
import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.UseCases.Messages.LerMensagensSecretariaUseCase;
import com.etec.zl.conecta.Application.UseCases.Turmas.*;
import com.etec.zl.conecta.Application.UseCases.Users.*;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("connecta/management")
@RequiredArgsConstructor
public class SecretariaController {

    private final AlterarTipoUseCase alterarTipoUseCase;
    private final DeletarUsuarioUseCase deletarUsuarioUseCase;
    private final NovosUsuariosUseCase novosUsuariosUseCase;
    private final SalvarUsuarioUseCase salvarUsuarioUseCase;
    private final SecretariaBuscaPorIdUseCase secretariaBuscaPorIdUseCase;
    private final SecretariaListagemPorCursantesUseCase secretariaListagemPorCursantesUseCase;
    private final SecretariaListagemPorFuncionariosUseCase secretariaListagemPorFuncionariosUseCase;
    private final SecretariaListagemPorNomeUseCase secretariaListagemPorNomeUseCase;
    private final SecretariaListagemPorTurmaUseCase secretariaListagemPorTurmaUseCase;
    private final SolicitarAlteracaoEmailUseCase solicitarAlteracaoEmailUseCase;
    private final SolicitarAlteracaoSenhaUseCase solicitarAlteracaoSenhaUseCase;
    private final EncontrarTurmaPorIdUseCase encontrarTurmaPorIdUseCase;
    private final ListarTodasAsTurmasAtuaisUseCase listarTodasAsTurmasAtuaisUseCase;
    private final ListarTodasAsTurmasUseCase listarTodasAsTurmasUseCase;
    private final ListarTurmasPorCursoAtuaisUseCase listarTurmasPorCursoAtuaisUseCase;
    private final ListarTurmasPorCursoUseCase listarTurmasPorCursoUseCase;
    private final NovasTurmasUseCase novasTurmasUseCase;
    private final PassaModuloUseCase passaModuloUseCase;
    private final LerMensagensSecretariaUseCase lerMensagensSecretariaUseCase;

    @GetMapping("/alunos")
    public PageResult<DTORetornoSecretaria> lerCursantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorCursantesUseCase.secretariaListagemPorCursantes(request);
    }

    @GetMapping("/alunos/nome/{nome}")
    public PageResult<DTORetornoSecretaria> lerPorNome(
            @PathVariable String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorNomeUseCase.secretariaListagemPorFuncionarios(new Name(nome), request);
    }

    @GetMapping("/alunos/turma/{idTurma}")
    public PageResult<DTORetornoSecretaria> lerPorTurma(
            @PathVariable UUID idTurma,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorTurmaUseCase.secretariaListagemPorTurma(idTurma, request);
    }

    @GetMapping("/funcionarios")
    public PageResult<DTORetornoSecretaria> lerFuncionarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorFuncionariosUseCase.secretariaListagemPorFuncionarios(request);
    }

    @GetMapping("/usuarios/{id}")
    public DTORetornoSecretaria buscarUsuario(@PathVariable String id) {
        return secretariaBuscaPorIdUseCase.secretariaBuscaPorId(id);
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public void salvarUsuario(@RequestBody DTOCadastro dto) {
        salvarUsuarioUseCase.salvarUsuario(dto);
    }

    @PostMapping("/usuarios/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public void salvarUsuariosLote(@RequestBody List<DTOCadastro> dtos) {
        novosUsuariosUseCase.novosUsuarios(dtos);
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable String id) {
        deletarUsuarioUseCase.deletarUsuario(id);
    }

    @PatchMapping("/usuarios/{id}/tipo")
    public void alterarTipo(@PathVariable String id, @RequestParam Tipo tipo) {
        alterarTipoUseCase.alterarTipo(id, tipo);
    }

    @PostMapping("/usuarios/{id}/solicitar-senha")
    public void solicitarSenha(@PathVariable String id) {
        solicitarAlteracaoSenhaUseCase.solicitarAlteracaoSenha(id);
    }

    @PostMapping("/usuarios/{id}/solicitar-email")
    public void solicitarEmail(@PathVariable String id) {
        solicitarAlteracaoEmailUseCase.solicitarAlteracaoEmail(id);
    }

    @GetMapping("/turmas")
    public PageResult<Turma> listarTurmas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTodasAsTurmasUseCase.findAllTurmas(request);
    }

    @GetMapping("/turmas/atuais")
    public PageResult<Turma> listarTurmasAtuais(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTodasAsTurmasAtuaisUseCase.findAllTurmasAtuais(request);
    }

    @GetMapping("/turmas/{id}")
    public Turma buscarTurma(@PathVariable UUID id) {
        return encontrarTurmaPorIdUseCase.findTurmaPorId(id);
    }

    @GetMapping("/turmas/curso")
    public PageResult<Turma> listarPorCurso(
            @RequestParam Cursos curso,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTurmasPorCursoUseCase.findAllTurmasByCurso(curso, request);
    }

    @GetMapping("/turmas/curso/{curso}/atuais")
    public PageResult<Turma> listarPorCursoAtuais(
            @PathVariable Cursos curso,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTurmasPorCursoAtuaisUseCase.findAllTurmasByCursoAtuais(curso, request);
    }

    @PostMapping("/turmas")
    @ResponseStatus(HttpStatus.CREATED)
    public void criarTurmas(@RequestBody List<DTOCadastroTurma> dtos) {
        novasTurmasUseCase.cadastroTurmas(dtos);
    }

    @PostMapping("/turmas/passar-modulo")
    public void proximoModulo() {
        passaModuloUseCase.passaModulo();
    }

    @GetMapping("/mensagens/sender/{sender}/receiver/{receiver}")
    public PageResult<DTOReturnMessageSecretaria> listarMensagens(
            @PathVariable String sender, @PathVariable String receiver,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return lerMensagensSecretariaUseCase.lerMensagensSecretaria(sender, receiver, request);
    }
}