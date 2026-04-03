package com.etec.zl.conecta.Application.DTOs.FAQs;

import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;

public record DTORegisterFAQ(String question, String answer, Prioridade relevance) {
}
