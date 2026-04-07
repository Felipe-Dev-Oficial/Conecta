package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.TurmaRepository;
import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.Cursos;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.Status;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers.TurmaAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaTurmaRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services.PaginationAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TurmaRepositoryAdapter implements TurmaRepository {

    private final JpaTurmaRepository externalRepository;
    private final TurmaAdapterMapper mapper;

    @Override
    @CacheEvict(value = "turmas", allEntries = true)
    public void save(Turma turma) {
        externalRepository.save(mapper.toEntity(turma));
    }

    @Override
    @CacheEvict(value = "turmas", allEntries = true)
    public void passaModulo() {
        externalRepository.passaModuloBaseadoNoCalendario();
    }

    @Override
    @Cacheable(value = "turmas", key = "#id")
    public Optional<Turma> findById(String id) {
        return externalRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Cacheable(value = "turmas", key = "'all-' + #pageable.page()")
    public PageResult<Turma> findAllTurmas(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAll(PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Cacheable(value = "turmas", key = "'on-' + #pageable.page()")
    public PageResult<Turma> findAllTurmasAtuais(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findByStatus(Status.ON, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Cacheable(value = "turmas", key = "#curso.name() + '-' + #pageable.page()")
    public PageResult<Turma> findAllTurmasByCurso(Cursos curso, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findByCurso(curso, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Cacheable(value = "turmas", key = "'on-' + #curso.name() + '-' + #pageable.page()")
    public PageResult<Turma> findAllTurmasByCursoAtuais(Cursos curso, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findByCursoAndStatusOn(curso, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }
}
