package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Solicitations.DTORequirement;
import com.etec.zl.conecta.Application.DTOs.Solicitations.DTOReturnRequirement;
import com.etec.zl.conecta.Application.Ports.Input.Solicitations.GetSelfSolicitationPort;
import com.etec.zl.conecta.Application.Ports.Input.Solicitations.SendSolicitationPort;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("conecta")
@RequiredArgsConstructor
public class SolicitationController {

    private final GetSelfSolicitationPort getSelfSolicitationPort;
    private final SendSolicitationPort sendSolicitationPort;


    @GetMapping("/solicitations")
    private PageResult<DTOReturnRequirement> getSolicitations(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ) {
        return getSelfSolicitationPort.getSolicitations(user.getId(), new PageRequest(page, size));
    }

    @PostMapping("/solicitations")
    private void sendSolicitation(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody DTORequirement dto
            ){
        sendSolicitationPort.sendSolicitation(user.getId(), dto);
    }
}
