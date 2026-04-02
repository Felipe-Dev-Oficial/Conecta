package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOUpdateFaq;
import com.etec.zl.conecta.Application.UseCases.FAQs.*;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("connecta/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final LerFAQUseCase lerFAQUseCase;
    //secretaria
    private final AlterarFAQUseCase alterarFAQUseCase;
    private final ApagarFAQUseCase apagarFAQUseCase;
    private final AumentarPrioridadeFAQUseCase aumentarPrioridadeFAQUseCase;
    private final DiminuirPrioridadeFAQUseCase diminuirPrioridadeFAQUseCase;
    private final EscreverFAQUseCase escreverFAQUseCase;
    private final LerFAQsSecretariaUseCase lerFAQsSecretariaUseCase;
    private final PublicarFAQUseCase publicarFAQUseCase;

    @GetMapping()
    public PageResult<DTOReturnFAQ> lerFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerFAQUseCase.lerFAQs(result);
    }
    @GetMapping("/management")
    public PageResult<FAQ> lerFaqsSecretaria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerFAQsSecretariaUseCase.lerFAQsSecretaria(result);
    }
    @PostMapping("/management")
    public void escreverFaq(@AuthenticationPrincipal UserPrincipal user, @RequestBody DTORegisterFAQ dto){
        escreverFAQUseCase.escreverFAQ(user.getId(), dto);
    }
    @PostMapping("/management/{id}")
    public void postarFaq(@PathVariable UUID id){
        publicarFAQUseCase.publicarFAQ(id);
    }
    @PatchMapping("/management/{id}")
    public void alterarFaq(@PathVariable UUID id, @RequestBody DTOUpdateFaq dto){
        alterarFAQUseCase.alterarFAQ(id, dto);
    }
    @DeleteMapping("/management/{id}")
    public void apagarFaq(@PathVariable UUID id){
        apagarFAQUseCase.apagarFAQ(id);
    }
    @PatchMapping("/management/{id}/relevance")
    public void aumentarRelevanciaFaq(@PathVariable UUID id){
        aumentarPrioridadeFAQUseCase.elevarPrioridadeFAQ(id);
    }
    @DeleteMapping("/management/{id}/relevance")
    public void redefinirRelevanciaFaq(@PathVariable UUID id){
        diminuirPrioridadeFAQUseCase.diminuirPrioridadeFAQ(id);
    }
}
