package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.FAQRepository;
import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.FAQAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoFAQRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services.PaginationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FAQRepositoryAdapter implements FAQRepository {

    private final MongoFAQRepository externalRepository;
    private final FAQAdapterMapper mapper;

    @Override
    @CacheEvict(value = "faqs", allEntries = true)
    public void save(FAQ faq) {
        externalRepository.save(mapper.toEntity(faq));
    }

    @Override
    public PageResult<FAQ> getAllActives(PageRequest pageable) {
        return PaginationAdapter.toDomain(
                externalRepository.findAllActives(PaginationAdapter.toSpring(pageable))
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Cacheable(value = "faqs", key = "'page-' + #pageable.page()")
    public PageResult<FAQ> getAll(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAll(PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Cacheable(value = "faq-single", key = "#idFaq")
    public Optional<FAQ> getById(UUID idFaq) {
        return externalRepository.findById(idFaq)
                .map(mapper::toDomain);
    }
}
