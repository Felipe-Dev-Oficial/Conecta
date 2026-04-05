package com.etec.zl.conecta.Infraestructure.Security.Models;

import com.etec.zl.conecta.Domain.ValueObjects.Password;

public record DTOLogin(String id, Password senha) {
}
