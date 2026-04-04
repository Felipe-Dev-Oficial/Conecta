package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOContatos;
import com.etec.zl.conecta.Application.DTOs.Messages.DTOInfoMessage;
import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;
import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessage;
import com.etec.zl.conecta.Application.Ports.Input.Messages.EnviarMensagemPort;
import com.etec.zl.conecta.Application.Ports.Input.Messages.LerMensagensPort;
import com.etec.zl.conecta.Application.Ports.Input.Messages.RetornarContatosPort;
import com.etec.zl.conecta.Application.UseCases.Messages.EnviarMensagemUseCase;
import com.etec.zl.conecta.Application.UseCases.Messages.LerMensagensUseCase;
import com.etec.zl.conecta.Application.UseCases.Messages.RetornarContatosUseCase;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conecta/mensagens")
@RequiredArgsConstructor
public class MensagensController {

    private final LerMensagensPort lerMensagensPort;
    private final EnviarMensagemPort enviarMensagemPort;
    private final RetornarContatosPort retornarContatosPort;

    @GetMapping("/contatos")
    public SliceResult<DTOContatos> contatos(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return retornarContatosPort.retornarContatos(user.getId(), result);
    }

    @GetMapping("/{id}")
    public PageResult<DTOReturnMessage> lerMensagens(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = new PageRequest(page, size);
        return lerMensagensPort.lerMensagens(user.getId(), id, result);
    }

    @PostMapping("/{id}")
    public void escreverMensagens(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String id,
            @RequestBody DTOInfoMessage dto) {
        var dtoRegisterMessage = new DTORegisterMessage(user.getId(), id, dto);
        enviarMensagemPort.enviarMensagem(dtoRegisterMessage);
    }
}
