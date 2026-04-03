package com.etec.zl.conecta.Application.Ports.Input.Users;

import com.etec.zl.conecta.Application.DTOs.Users.DTORetornoSecretaria;
import com.etec.zl.conecta.Domain.ValueObjects.Name;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecretariaListagemPorNomePort {

    PageResult<DTORetornoSecretaria> secretariaListagemPorFuncionarios(Name nome, PageRequest pageable);
}
