package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.UseCases.Users.ProfessorBuscaPorIdUseCase;
import com.etec.zl.conecta.Application.UseCases.Users.ProfessorListagemPorNomeUseCase;
import com.etec.zl.conecta.Application.UseCases.Users.ProfessorListagemPorTurmaUseCase;
import com.etec.zl.conecta.Application.UseCases.Users.ProfessorListagemUseCase;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("connecta/professores")
@RequiredArgsConstructor
public class ProfessorController {
    private final ProfessorListagemPorTurmaUseCase professorListagemPorTurmaUseCase;
    private final ProfessorListagemPorNomeUseCase professorListagemPorNomeUseCase;
    private final ProfessorBuscaPorIdUseCase professorBuscaPorIdUseCase;
    private final ProfessorListagemUseCase professorListagemUseCase;

    @GetMapping()
    public PageResult<DTORetornoNormal> retornarAlunos(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return professorListagemUseCase.findAllAlunos(user.getId(), result);
    }
    @GetMapping("/aluno/id/{id}")
    public DTORetornoNormal retornarAlunos(@AuthenticationPrincipal UserPrincipal user, @PathVariable String id) {
        return professorBuscaPorIdUseCase.professorBuscaPorId(user.getId(), id);
    }
    @GetMapping("/aluno/nome/{nome}")
    public PageResult<DTORetornoNormal> retornarAlunosPorNome(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        var nomeVO = new Name(nome);
        return professorListagemPorNomeUseCase.professorListagemPorNome(user.getId(), nomeVO, result);
    }
    @GetMapping("/turma/{id}")
    public PageResult<DTORetornoNormal> retornarTurma(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return professorListagemPorTurmaUseCase.professorListagemPorTurma(user.getId(), id, result);
    }
}
