package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories;

import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.SolicitationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.UUID;

public interface MongoSolicitationRepository extends MongoRepository<SolicitationEntity, UUID> {

    @Query("{ 'id_soliciter': ?0 }")
    Page<SolicitationEntity> findBySolicitatorId(String id, Pageable pageable);
    Page<SolicitationEntity> findByNomeStartingWith(String nome, Pageable pageable);
}
