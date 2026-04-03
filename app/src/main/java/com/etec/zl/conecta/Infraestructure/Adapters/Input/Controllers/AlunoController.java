package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.UseCases.Users.AlunoBuscaProfessorPorIdUseCase;
import com.etec.zl.conecta.Application.UseCases.Users.AlunoListagemProfessorUseCase;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("connecta/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoBuscaProfessorPorIdUseCase alunoBuscaProfessorPorIdUseCase;
    private final AlunoListagemProfessorUseCase alunoListagemProfessorUseCase;

    @GetMapping("/busca_professor")
    public PageResult<DTORetornoNormal> buscarProfessores(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        var request = new PageRequest(page, size);
        return alunoListagemProfessorUseCase.alunoListagemProfessor(user.getId(), request);
    }
    @GetMapping("/busca_professor/{id}")
    public DTORetornoNormal buscarProfessores(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id
        ){
        return alunoBuscaProfessorPorIdUseCase.alunoBuscaProfessorPorId(user.getId(), id);
    }
}
