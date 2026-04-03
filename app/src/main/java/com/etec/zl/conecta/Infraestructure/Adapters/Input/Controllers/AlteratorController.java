package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.UseCases.Users.AlterarEmailUseCase;
import com.etec.zl.conecta.Application.UseCases.Users.AlterarSenhaUseCase;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import com.etec.zl.conecta.Domain.ValueObjects.Password;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("connecta/alterador")
@RequiredArgsConstructor
public class AlteratorController {

    private final AlterarEmailUseCase alterarEmailUseCase;
    private final AlterarSenhaUseCase alterarSenhaUseCase;

    @PatchMapping("/email")
    public void confirmarEmail(@RequestParam String id, @RequestParam UUID token, @RequestParam String email) {
        alterarEmailUseCase.alterarEmail(id, token, new Email(email));
    }

    @PatchMapping("/senha")
    public void confirmarSenha(@RequestParam String id, @RequestParam UUID token, @RequestParam String senha) {
        alterarSenhaUseCase.alterarSenha(id, token, new Password(senha));
    }
}
