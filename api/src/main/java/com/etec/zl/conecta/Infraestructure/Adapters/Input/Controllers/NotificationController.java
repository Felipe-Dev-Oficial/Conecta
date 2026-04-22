package com.etec.zl.conecta.Infraestructure.Adapters.Input.Controllers;

import com.etec.zl.conecta.Application.DTOs.Users.DTONotificador;
import com.etec.zl.conecta.Application.Ports.Input.Users.VincularNotificadorPort;
import com.etec.zl.conecta.Infraestructure.Security.Models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("conecta/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final VincularNotificadorPort vincularNotificadorPort;

    @Value("${VAPID_PUBLIC_KEY}")
    private String vapidPublicKey;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void vincularNotificador(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody DTONotificador dto) {
        vincularNotificadorPort.vincular(user.getId(), dto.endpoint(), dto.p256dh(), dto.auth());
    }

    @GetMapping("/vapid-public-key")
    public String getVapidPublicKey() {
        return vapidPublicKey;
    }
}