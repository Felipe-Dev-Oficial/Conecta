package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAlteraAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.UseCases.Statements.*;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("connecta/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final LerAnuncioUseCase lerAnuncioUseCase;
    //secretaria
    private final AlterarAnuncioUseCase alterarAnuncioUseCase;
    private final ApagarAnuncioUseCase apagarAnuncioUseCase;
    private final ElevarPrioridadeAnuncioUseCase elevarPrioridadeAnuncioUseCase;
    private final GerarAnuncioUseCase gerarAnuncioUseCase;
    private final LerAnuncioSecretariaUseCase lerAnuncioSecretariaUseCase;
    private final ReduzirPrioridadeAnuncioUseCase reduzirPrioridadeAnuncioUseCase;

    @GetMapping()
    public PageResult<DTORetornoAnuncio> retornarAnuncios(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerAnuncioUseCase.lerAnuncios(user.getId(), result);
    }
    @GetMapping("/management")
    public PageResult<Statement> retornoAnunciosSecretaria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerAnuncioSecretariaUseCase.lerAnunciosSecretaria(result);
    }
    @PostMapping("/management")
    public void gerarAnuncio(@AuthenticationPrincipal UserPrincipal user, @RequestBody DTOAnuncio dto){
        gerarAnuncioUseCase.gerarAnuncio(user.getId(), dto);
    }
    @PatchMapping("management/{id}")
    public void alterarAnuncio(@PathVariable UUID id, @RequestBody DTOAlteraAnuncio dto){
        alterarAnuncioUseCase.alterarAnuncio(id, dto);
    }
    @PatchMapping("management/{id}/prioridade")
    public void elevarProridadeAnuncio(@PathVariable UUID id){
        elevarPrioridadeAnuncioUseCase.elevarPrioridadeAnuncio(id);
    }
    @DeleteMapping("management/{id}/prioridade")
    public void reduzirProridadeAnuncio(@PathVariable UUID id){
        reduzirPrioridadeAnuncioUseCase.reduzirPrioridadeAnuncio(id);
    }
    @DeleteMapping("management/{id}")
    public void apagarAnuncio(@PathVariable UUID id){
        apagarAnuncioUseCase.apagarAnuncio(id);
    }
}
