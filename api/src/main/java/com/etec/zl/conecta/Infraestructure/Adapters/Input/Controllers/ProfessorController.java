package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Ports.Input.Users.*;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("conecta/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorListagemPorTurmaPort professorListagemPorTurmaPort;
    private final ProfessorListagemPorNomePort professorListagemPorNomePort;
    private final ProfessorBuscaPorIdPort professorBuscaPorIdPort;
    private final ProfessorListagemPort professorListagemPort;

    @GetMapping()
    public PageResult<DTORetornoNormal> retornarAlunos(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return professorListagemPort.findAllAlunos(user.getId(), result);
    }

    @GetMapping("/aluno/id/{id}")
    public DTORetornoNormal retornarAlunoPorId(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id) {
        return professorBuscaPorIdPort.professorBuscaPorId(user.getId(), id);
    }

    @GetMapping("/aluno/nome/{nome}")
    public PageResult<DTORetornoNormal> retornarAlunosPorNome(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        var nomeVO = new Name(nome);
        return professorListagemPorNomePort.professorListagemPorNome(user.getId(), nomeVO, result);
    }

    @GetMapping("/turma/{id}")
    public PageResult<DTORetornoNormal> retornarTurma(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return professorListagemPorTurmaPort.professorListagemPorTurma(user.getId(), id, result);
    }
}
