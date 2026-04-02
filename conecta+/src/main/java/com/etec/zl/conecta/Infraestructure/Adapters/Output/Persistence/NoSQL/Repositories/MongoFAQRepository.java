package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories;

import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.UUID;

public interface MongoFAQRepository extends MongoRepository<FAQEntity, UUID> {
    @Query("{ 'status': 'PUBLICADO' }")
    Page<FAQEntity> findAllActives(Pageable pageable);
    Page<FAQEntity> findAll(Pageable pageable);
    UUID id(UUID id);
}
