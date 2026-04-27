package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.SolicitationRepository;
import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.SolicitationAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoSolicitationRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services.PaginationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SolicitationRepositoryAdapter implements SolicitationRepository {

    private final MongoSolicitationRepository externalRepository;
    private final SolicitationAdapterMapper mapper;

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    @Override
    @Caching(evict = {
            @CacheEvict(value = "solicitations", allEntries = true)
    })
    public void saveSolicitation(Solicitation solicitation) {
        externalRepository.save(mapper.toEntity(solicitation));
    }

    @Override
    @Cacheable(value = "solicitations", key = "#id")
    public Optional<Solicitation> getSolicitationById(UUID id) {
        return externalRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Cacheable(value = "solicitations", key = "'user-' + #id + '-' + #pageRequest.page()")
    public PageResult<Solicitation> getSolicitationsByUser(String id, PageRequest pageRequest) {
        return PaginationAdapter.toDomain(
                externalRepository.findBySolicitatorId(
                        id,
                        PaginationAdapter.toSpring(pageRequest, DEFAULT_SORT)
                ).map(mapper::toDomain)
        );
    }

    @Override
    @Cacheable(value = "solicitations", key = "'search-' + #search + '-' + #pageRequest.page()")
    public PageResult<Solicitation> getSolicitationsBySearch(String search, PageRequest pageRequest) {
        org.springframework.data.domain.Pageable pageable = PaginationAdapter.toSpring(pageRequest, DEFAULT_SORT);

        if (externalRepository.findBySolicitatorId(search, pageable).hasContent()) {
            return PaginationAdapter.toDomain(
                    externalRepository.findBySolicitatorId(search, pageable)
                            .map(mapper::toDomain)
            );
        } else {
            return PaginationAdapter.toDomain(
                    externalRepository.findByNomeStartingWith(search, pageable)
                            .map(mapper::toDomain)
            );
        }
    }
}