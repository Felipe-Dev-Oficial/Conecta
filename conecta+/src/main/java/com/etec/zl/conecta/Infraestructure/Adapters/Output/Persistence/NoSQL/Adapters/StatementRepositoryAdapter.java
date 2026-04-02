package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.StatementRepository;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Mappers.StatementAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoStatementRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services.PaginationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StatementRepositoryAdapter implements StatementRepository {

    private final MongoStatementRepository externalRepository;
    private final StatementAdapterMapper mapper;

    @Override
//    @CacheEvict(value = "statements", allEntries = true)
    public void save(Statement statement) {
        externalRepository.save(mapper.toEntity(statement));
    }

    @Override
//    @Cacheable(value = "statements", key = "#id")
    public Optional<Statement> findById(UUID id) {
        return externalRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<Statement> findAllStatements(PageRequest pageable) {
        return PaginationAdapter.toDomain(
                externalRepository.findAllStatements(
                        PaginationAdapter.toSpring(pageable)
                ).map(mapper::toDomain)
        );
    }

    @Override
    public PageResult<Statement> findStatements(DTOLeitura dto, PageRequest pageable) {
        Pageable springPage = PaginationAdapter.toSpring(pageable);

        Page<StatementEntity> result = switch (dto.tipo().name()) {
            case "ALUNO"     -> externalRepository.findStatementsForAluno(
                    dto.turmas() != null ? dto.turmas() : List.of(), springPage);
            case "PROFESSOR" -> externalRepository.findStatementsForProfessor(springPage);
            case "EX_ALUNO"  -> externalRepository.findStatementsForExAluno(springPage);
            default          -> externalRepository.findAllStatements(springPage);
        };

        return PaginationAdapter.toDomain(result.map(mapper::toDomain));
    }
}
