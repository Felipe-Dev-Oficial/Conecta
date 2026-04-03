package com.etec.zl.conecta.Application.Ports.Input.Messages;

import com.etec.zl.conecta.Application.DTOs.Messages.DTOContatos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;

public interface RetornarContatosPort {

    SliceResult<DTOContatos> retornarContatos(String id, PageRequest pageable);
}
