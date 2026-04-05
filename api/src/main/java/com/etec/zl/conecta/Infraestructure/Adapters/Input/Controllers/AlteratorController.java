package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.Ports.Input.Users.AlterarEmailPort;
import com.etec.zl.conecta.Application.Ports.Input.Users.AlterarSenhaPort;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("conecta/alterador")
@RequiredArgsConstructor
public class AlteratorController {

    private final AlterarEmailPort alterarEmailPort;
    private final AlterarSenhaPort alterarSenhaPort;

    @PatchMapping("/email")
    public void confirmarEmail(@RequestParam String id, @RequestParam UUID token, @RequestParam String email) {
        alterarEmailPort.alterarEmail(id, token, new Email(email));
    }

    @PatchMapping("/senha")
    public void confirmarSenha(@RequestParam String id, @RequestParam UUID token, @RequestParam String senha) {
        alterarSenhaPort.alterarSenha(id, token, new Password(senha));
    }
}
