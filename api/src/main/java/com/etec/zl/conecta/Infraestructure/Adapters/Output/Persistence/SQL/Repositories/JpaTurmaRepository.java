package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories;

import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.Status;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.TurmaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface JpaTurmaRepository extends JpaRepository<TurmaEntity, String> {

    Page<TurmaEntity> findByStatus(Status status, Pageable pageable);

    Page<TurmaEntity> findByCurso(Cursos curso, Pageable pageable);

    @Query("SELECT t FROM TurmaEntity t WHERE t.curso = :curso AND t.status = 'ON'")
    Page<TurmaEntity> findByCursoAndStatusOn(@Param("curso") Cursos curso, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE turmas 
    SET atual = atual + 1 
    WHERE status = 'ON' 
    AND atual < modulos
    AND (
        (extract(month from now()) BETWEEN 7 AND 8 
        AND curso NOT LIKE '%M_TEC' 
        AND curso NOT LIKE '%AMS')
        
        OR 
        
        (extract(month from now()) IN (12, 1, 2))
    )
    """, nativeQuery = true)
    void passaModuloBaseadoNoCalendario();
}
