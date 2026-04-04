package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessageSecretaria;
import com.etec.zl.conecta.Application.DTOs.Turmas.DTOCadastroTurma;
import com.etec.zl.conecta.Application.DTOs.Users.DTOCadastro;
import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Application.Ports.Input.Messages.LerMensagensSecretariaPort;
import com.etec.zl.conecta.Application.Ports.Input.Turmas.*;
import com.etec.zl.conecta.Application.Ports.Input.Users.*;
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

    private final AlterarTipoPort alterarTipoPort;
    private final DeletarUsuarioPort deletarUsuarioPort;
    private final NovosUsuariosPort novosUsuariosPort;
    private final SalvarUsuarioPort salvarUsuarioPort;
    private final SecretariaBuscaPorIdPort secretariaBuscaPorIdPort;
    private final SecretariaListagemPorCursantesPort secretariaListagemPorCursantesPort;
    private final SecretariaListagemPorFuncionariosPort secretariaListagemPorFuncionariosPort;
    private final SecretariaListagemPorNomePort secretariaListagemPorNomePort;
    private final SecretariaListagemPorTurmaPort secretariaListagemPorTurmaPort;
    private final SolicitarAlteracaoEmailPort solicitarAlteracaoEmailPort;
    private final SolicitarAlteracaoSenhaPort solicitarAlteracaoSenhaPort;
    private final EncontraTurmaPorIdPort encontraTurmaPorIdPort;
    private final ListarTodasAsTurmasAtuaisPort listarTodasAsTurmasAtuaisPort;
    private final ListarTodasAsTurmasPort listarTodasAsTurmasPort;
    private final ListarTurmasPorCursoAtuaisPort listarTurmasPorCursoAtuaisPort;
    private final ListarTurmasPorCursoPort listarTurmasPorCursoPort;
    private final NovasTurmasPort novasTurmasPort;
    private final PassaModuloPort passaModuloPort;
    private final LerMensagensSecretariaPort lerMensagensSecretariaPort;

    @GetMapping("/alunos")
    public PageResult<DTORetornoSecretaria> lerCursantes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorCursantesPort.secretariaListagemPorCursantes(request);
    }

    @GetMapping("/alunos/nome/{nome}")
    public PageResult<DTORetornoSecretaria> lerPorNome(
            @PathVariable String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorNomePort.secretariaListagemPorFuncionarios(new Name(nome), request);
    }

    @GetMapping("/alunos/turma/{idTurma}")
    public PageResult<DTORetornoSecretaria> lerPorTurma(
            @PathVariable UUID idTurma,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorTurmaPort.secretariaListagemPorTurma(idTurma, request);
    }

    @GetMapping("/funcionarios")
    public PageResult<DTORetornoSecretaria> lerFuncionarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return secretariaListagemPorFuncionariosPort.secretariaListagemPorFuncionarios(request);
    }

    @GetMapping("/usuarios/{id}")
    public DTORetornoSecretaria buscarUsuario(@PathVariable String id) {
        return secretariaBuscaPorIdPort.secretariaBuscaPorId(id);
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public void salvarUsuario(@RequestBody DTOCadastro dto) {
        salvarUsuarioPort.salvarUsuario(dto);
    }

    @PostMapping("/usuarios/lote")
    @ResponseStatus(HttpStatus.CREATED)
    public void salvarUsuariosLote(@RequestBody List<DTOCadastro> dtos) {
        novosUsuariosPort.novosUsuarios(dtos);
    }

    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable String id) {
        deletarUsuarioPort.deletarUsuario(id);
    }

    @PatchMapping("/usuarios/{id}/tipo")
    public void alterarTipo(@PathVariable String id, @RequestParam Tipo tipo) {
        alterarTipoPort.alterarTipo(id, tipo);
    }

    @PostMapping("/usuarios/{id}/solicitar-senha")
    public void solicitarSenha(@PathVariable String id) {
        solicitarAlteracaoSenhaPort.solicitarAlteracaoSenha(id);
    }

    @PostMapping("/usuarios/{id}/solicitar-email")
    public void solicitarEmail(@PathVariable String id) {
        solicitarAlteracaoEmailPort.solicitarAlteracaoEmail(id);
    }

    @GetMapping("/turmas")
    public PageResult<Turma> listarTurmas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTodasAsTurmasPort.findAllTurmas(request);
    }

    @GetMapping("/turmas/atuais")
    public PageResult<Turma> listarTurmasAtuais(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTodasAsTurmasAtuaisPort.findAllTurmasAtuais(request);
    }

    @GetMapping("/turmas/{id}")
    public Turma buscarTurma(@PathVariable UUID id) {
        return encontraTurmaPorIdPort.findTurmaPorId(id);
    }

    @GetMapping("/turmas/curso")
    public PageResult<Turma> listarPorCurso(
            @RequestParam Cursos curso,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTurmasPorCursoPort.findAllTurmasByCurso(curso, request);
    }

    @GetMapping("/turmas/curso/{curso}/atuais")
    public PageResult<Turma> listarPorCursoAtuais(
            @PathVariable Cursos curso,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var request = new PageRequest(page, size);
        return listarTurmasPorCursoAtuaisPort.findAllTurmasByCursoAtuais(curso, request);
    }

    @PostMapping("/turmas")
    @ResponseStatus(HttpStatus.CREATED)
    public void criarTurmas(@RequestBody List<DTOCadastroTurma> dtos) {
        novasTurmasPort.cadastroTurmas(dtos);
    }

    @PostMapping("/turmas/passar-modulo")
    public void proximoModulo() {
        passaModuloPort.passaModulo();
    }

    @GetMapping("/mensagens/sender/{sender}/receiver/{receiver}")
    public PageResult<DTOReturnMessageSecretaria> listarMensagens(
            @PathVariable String sender,
            @PathVariable String receiver,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return lerMensagensSecretariaPort.lerMensagensSecretaria(sender, receiver, request);
    }
}