package com.etec.zl.conecta.Domain.ValueObjects;

public record Notificador(
        Long id,
        String endpoint,
        String p256dh,
        String auth
) {}