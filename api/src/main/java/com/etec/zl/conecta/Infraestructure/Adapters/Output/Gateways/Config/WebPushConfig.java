package com.etec.zl.conecta.Infraestructure.Adapters.Output.Gateways.Config;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class WebPushConfig {

    @Value("${VAPID_PUBLIC_KEY}")
    private String publicKey;

    @Value("${VAPID_PRIVATE_KEY}")
    private String privateKey;

    @Value("${VAPID_SUBJECT}")
    private String subject;

    @Bean
    public PushService pushService() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        return new PushService()
            .setPublicKey(publicKey)
            .setPrivateKey(privateKey)
            .setSubject(subject);
    }
}