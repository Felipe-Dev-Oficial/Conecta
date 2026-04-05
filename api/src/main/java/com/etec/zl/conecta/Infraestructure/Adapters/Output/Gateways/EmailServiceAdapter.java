package com.etec.zl.conecta.Infraestructure.Adapters.Output.Gateways;


import com.etec.zl.conecta.Application.Ports.Output.Services.EmailService;
import com.etec.zl.conecta.Domain.ValueObjects.Email;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailServiceAdapter implements EmailService {

    private final JavaMailSender mailSender;


    public EmailServiceAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String token, Email email, String subject) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("e211acad@cps.sp.gov.br");
        message.setTo(email.email());
        message.setSubject(subject);
        message.setText(token);

        mailSender.send(message);
    }
}
