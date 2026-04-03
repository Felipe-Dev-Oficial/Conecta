package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Infraestructure.Security.Models.DTOLogin;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import com.etec.zl.conecta.Infraestructure.Security.Service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("connecta/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody @Valid DTOLogin dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.id(), dados.senha().password());

        var authentication = manager.authenticate(authenticationToken);

        var principal = (UserPrincipal) authentication.getPrincipal();

        var tokenJWT = tokenService.generateToken(principal.user());

        return ResponseEntity.ok(tokenJWT);
    }
}