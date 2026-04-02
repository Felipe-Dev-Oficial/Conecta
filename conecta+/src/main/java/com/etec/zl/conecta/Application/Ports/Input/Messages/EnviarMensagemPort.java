package com.etec.zl.conecta.Application.Ports.Input.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTORegisterMessage;

public interface EnviarMensagemPort {

    void enviarMensagem(DTORegisterMessage dto);
}
