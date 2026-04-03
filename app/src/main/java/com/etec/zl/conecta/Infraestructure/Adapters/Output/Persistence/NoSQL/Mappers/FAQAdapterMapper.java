package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import org.springframework.stereotype.Component;

@Component
public class FAQAdapterMapper {

    public FAQ toDomain(FAQEntity entity) {
        return new FAQ(
                entity.getId(),
                entity.getQuestion(),
                entity.getAnswer(),
                entity.getAuthorId(),
                entity.getStatusFAQ(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getRelevance()
        );
    }
    public FAQEntity toEntity(FAQ domain) {
        return new FAQEntity(
                domain.getId(),
                domain.getAuthorId(),
                domain.getQuestion(),
                domain.getAnswer(),
                domain.getStatusFAQ(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getRelevance()
        );
    }
}
