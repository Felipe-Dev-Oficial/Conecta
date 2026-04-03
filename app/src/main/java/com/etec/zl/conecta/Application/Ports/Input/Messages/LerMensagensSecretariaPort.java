package com.etec.zl.conecta.Application.Ports.Input.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessageSecretaria;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

public interface LerMensagensSecretariaPort {

    PageResult<DTOReturnMessageSecretaria> lerMensagensSecretaria(String idSender, String idReceiver, PageRequest pageable);
}
