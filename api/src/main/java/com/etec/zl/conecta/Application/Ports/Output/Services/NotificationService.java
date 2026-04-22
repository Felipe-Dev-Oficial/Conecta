package com.etec.zl.conecta.Application.Ports.Output.Services;

import com.etec.zl.conecta.Domain.ValueObjects.Notificador;

import java.util.List;

public interface NotificationService {
        void sendNotifications(List<Notificador> notificadores, String title, String body);
}
