package com.etec.zl.conecta.Application.Ports.Output.Services;

import com.etec.zl.conecta.Domain.ValueObjects.Email;

public interface EmailService {

    void send(String content, Email email, String subject);
}
