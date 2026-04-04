package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOAlteraAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTOAnuncio;
import com.etec.zl.conecta.Application.DTOs.Statements.DTORetornoAnuncio;
import com.etec.zl.conecta.Application.Ports.Input.Statements.*;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("conecta/anuncios")
@RequiredArgsConstructor
public class AnuncioController {

    private final LerAnuncioPort lerAnuncioPort;
    private final AlterarAnuncioPort alterarAnuncioPort;
    private final ApagarAnuncioPort apagarAnuncioPort;
    private final ElevarPrioridadeAnuncioPort elevarPrioridadeAnuncioPort;
    private final GerarAnuncioPort gerarAnuncioPort;
    private final LerAnuncioSecretariaPort lerAnuncioSecretariaPort;
    private final ReduzirPrioridadeAnuncioPort reduzirPrioridadeAnuncioPort;

    @GetMapping()
    public PageResult<DTORetornoAnuncio> retornarAnuncios(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerAnuncioPort.lerAnuncios(user.getId(), result);
    }

    @GetMapping("/management")
    public PageResult<Statement> retornoAnunciosSecretaria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerAnuncioSecretariaPort.lerAnunciosSecretaria(result);
    }

    @PostMapping("/management")
    public void gerarAnuncio(@AuthenticationPrincipal UserPrincipal user, @RequestBody DTOAnuncio dto) {
        gerarAnuncioPort.gerarAnuncio(user.getId(), dto);
    }

    @PatchMapping("management/{id}")
    public void alterarAnuncio(@PathVariable UUID id, @RequestBody DTOAlteraAnuncio dto) {
        alterarAnuncioPort.alterarAnuncio(id, dto);
    }

    @PatchMapping("management/{id}/prioridade")
    public void elevarPrioridadeAnuncio(@PathVariable UUID id) {
        elevarPrioridadeAnuncioPort.elevarPrioridadeAnuncio(id);
    }

    @DeleteMapping("management/{id}/prioridade")
    public void reduzirPrioridadeAnuncio(@PathVariable UUID id) {
        reduzirPrioridadeAnuncioPort.reduzirPrioridadeAnuncio(id);
    }

    @DeleteMapping("management/{id}")
    public void apagarAnuncio(@PathVariable UUID id) {
        apagarAnuncioPort.apagarAnuncio(id);
    }
}
