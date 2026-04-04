package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoNormal;
import com.etec.zl.conecta.Application.Ports.Input.Users.AlunoBuscaProfessorPorIdPort;
import com.etec.zl.conecta.Application.Ports.Input.Users.AlunoListagemProfessorPort;
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

    private final AlunoBuscaProfessorPorIdPort alunoBuscaProfessorPorIdPort;
    private final AlunoListagemProfessorPort alunoListagemProfessorPort;

    @GetMapping("/busca_professor")
    public PageResult<DTORetornoNormal> buscarProfessores(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var request = new PageRequest(page, size);
        return alunoListagemProfessorPort.alunoListagemProfessor(user.getId(), request);
    }

    @GetMapping("/busca_professor/{id}")
    public DTORetornoNormal buscarProfessorPorId(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id
    ) {
        return alunoBuscaProfessorPorIdPort.alunoBuscaProfessorPorId(user.getId(), id);
    }
}