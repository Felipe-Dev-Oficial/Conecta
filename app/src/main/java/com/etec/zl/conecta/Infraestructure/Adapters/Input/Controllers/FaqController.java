package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.FAQs.DTORegisterFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOReturnFAQ;
import com.etec.zl.conecta.Application.DTOs.FAQs.DTOUpdateFaq;
import com.etec.zl.conecta.Application.Ports.Input.FAQs.*;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("conecta/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final LerFAQPort lerFAQPort;
    private final AlterarFAQPort alterarFAQPort;
    private final ApagarFAQPort apagarFAQPort;
    private final AumentarPrioridadeFAQPort aumentarPrioridadeFAQPort;
    private final DiminuirPrioridadeFAQPort diminuirPrioridadeFAQPort;
    private final EscreverFAQPort escreverFAQPort;
    private final LerFAQsSecretariaPort lerFAQsSecretariaPort;
    private final PublicarFAQPort publicarFAQPort;

    @GetMapping()
    public PageResult<DTOReturnFAQ> lerFaqs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerFAQPort.lerFAQs(result);
    }

    @GetMapping("/management")
    public PageResult<FAQ> lerFaqsSecretaria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerFAQsSecretariaPort.lerFAQsSecretaria(result);
    }

    @PostMapping("/management")
    public void escreverFaq(@AuthenticationPrincipal UserPrincipal user, @RequestBody DTORegisterFAQ dto) {
        escreverFAQPort.escreverFAQ(user.getId(), dto);
    }

    @PostMapping("/management/{id}")
    public void postarFaq(@PathVariable UUID id) {
        publicarFAQPort.publicarFAQ(id);
    }

    @PatchMapping("/management/{id}")
    public void alterarFaq(@PathVariable UUID id, @RequestBody DTOUpdateFaq dto) {
        alterarFAQPort.alterarFAQ(id, dto);
    }

    @DeleteMapping("/management/{id}")
    public void apagarFaq(@PathVariable UUID id) {
        apagarFAQPort.apagarFAQ(id);
    }

    @PatchMapping("/management/{id}/relevance")
    public void aumentarRelevanciaFaq(@PathVariable UUID id) {
        aumentarPrioridadeFAQPort.elevarPrioridadeFAQ(id);
    }

    @DeleteMapping("/management/{id}/relevance")
    public void redefinirRelevanciaFaq(@PathVariable UUID id) {
        diminuirPrioridadeFAQPort.diminuirPrioridadeFAQ(id);
    }
}
