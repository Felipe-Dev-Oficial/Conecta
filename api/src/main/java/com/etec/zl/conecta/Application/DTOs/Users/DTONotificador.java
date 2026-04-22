package com.etec.zl.conecta.Application.DTOs.Users;

public record DTONotificador(
    String endpoint,
    String p256dh,
    String auth
) {}