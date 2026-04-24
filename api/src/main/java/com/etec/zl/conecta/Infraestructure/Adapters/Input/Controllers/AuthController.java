package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.Ports.Input.Users.DeletarNotificadorPort;
import com.etec.zl.conecta.Infraestructure.Security.Models.DTOLogin;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import com.etec.zl.conecta.Infraestructure.Security.Service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("conecta/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final DeletarNotificadorPort deletarNotificadorPort;

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody @Valid DTOLogin dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.id(), dados.senha().password());

        var authentication = manager.authenticate(authenticationToken);

        var principal = (UserPrincipal) authentication.getPrincipal();

        var tokenJWT = tokenService.generateToken(principal.user());

        return ResponseEntity.ok(tokenJWT);
    }

    @DeleteMapping("/logout")
    public void logOut(@AuthenticationPrincipal UserPrincipal user, String endpoint) {
        deletarNotificadorPort.deleteNotificador(user.getId(), endpoint);
    }
}