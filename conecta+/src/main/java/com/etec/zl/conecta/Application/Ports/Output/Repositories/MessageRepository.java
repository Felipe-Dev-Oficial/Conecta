package com.etec.zl.conecta.Application.Ports.Output.Repositories;

import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;

public interface MessageRepository {

    void save(Message message);
    PageResult<Message> ListarMensagens(String id, String idReceiver, PageRequest pageable);
    PageResult<Message> ListarMensagensSecretaria(String idSender, String idReceiver, PageRequest pageable);
    SliceResult<String> contatos(String id, PageRequest pageable);
}
