package com.etec.zl.conecta.Infraestructure.Adapters.Output.Gateways.Gateways;

import com.etec.zl.conecta.Application.Ports.Output.Services.NotificationService;
import com.etec.zl.conecta.Domain.ValueObjects.Notificador;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceAdapter implements NotificationService {

    private final PushService pushService;

    @Async("notificationExecutor")
    @Override
    public void sendNotifications(List<Notificador> notificadores, String title, String body) {
        notificadores.forEach(notificador -> {
            try {
                String payload = """
                    {"title":"%s","body":"%s","icon":"/assets/icons/icon-128x128.png"}
                    """.formatted(title, body);

                var subscription = new Subscription(
                        notificador.endpoint(),
                        new Subscription.Keys(notificador.p256dh(), notificador.auth())
                );

                pushService.send(new Notification(subscription, payload));
                log.info("Notificação enviada para {}", notificador.endpoint());

            } catch (Exception e) {
                log.error("Falha ao enviar para {}: {}", notificador.endpoint(), e.getMessage());
            }
        });
    }
}