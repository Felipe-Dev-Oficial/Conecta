package com.etec.zl.conecta.Application.Ports.Input.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOReturnMessage;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;

public interface LerMensagensPort {

    PageResult<DTOReturnMessage> lerMensagens(String idSender, String idReceiver, PageRequest pageable);
}
