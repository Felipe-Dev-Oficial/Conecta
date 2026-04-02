package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories;

import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MongoStatementRepository extends MongoRepository<StatementEntity, UUID> {

    String SORT = "{ 'priority': -1, 'sent_at': -1 }";

    @Query(value = """
    { '$and': [ { 'announcement_status': 'ON' }, { '$or': [ { 'target_info.targetType': 'GERAL' }, { 'target_info.targetType': 'ALUNOS' }, { '$and': [ { 'target_info.targetType': { '$in': ['TURMA', 'TURMAS'] } }, { 'target_info.targetIds': { '$in': ?0 } } ] } ] } ] }
    """, sort = SORT)
    Page<StatementEntity> findStatementsForAluno(List<UUID> turmasIds, Pageable pageable);

    @Query(value = """
    { '$and': [ { 'announcement_status': 'ON' }, { '$or': [ { 'target_info.targetType': 'GERAL' }, { 'target_info.targetType': 'PROFESSORES' } ] } ] }
    """, sort = SORT)
    Page<StatementEntity> findStatementsForProfessor(Pageable pageable);

    @Query(value = """
    { '$and': [ { 'announcement_status': 'ON' }, { '$or': [ { 'target_info.targetType': 'GERAL' }, { 'target_info.targetType': 'EX_ALUNOS' } ] } ] }
    """, sort = SORT)
    Page<StatementEntity> findStatementsForExAluno(Pageable pageable);

    @Query(value = "{}", sort = SORT)
    Page<StatementEntity> findAllStatements(Pageable pageable);
}
