package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters;

import com.etec.zl.conecta.Application.Ports.Output.Repositories.UserRepository;
import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.Exceptions.UserNotFoundException;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.NotificadorEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Mappers.UserAdapterMapper;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaUserRepository;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Projection.NotificadorProjection;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.Services.PaginationAdapter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository externalRepository;
    private final UserAdapterMapper mapper;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#user.id"),
            @CacheEvict(value = "users-lists", allEntries = true),
            @CacheEvict(value = "users-context", allEntries = true),
            @CacheEvict(value = "users-tokens", allEntries = true)
    })
    public void save(User user) {
        externalRepository.save(mapper.toEntity(user));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return externalRepository.findByEmail(email.email())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    @Cacheable(value = "users-context", key = "'turmas-' + #id")
    public List<UUID> retornarTurmasRelacionadas(String id) {
        return externalRepository.findRelatedTurmasIds(id)
                .stream()
                .map(UUID::fromString)
                .toList();
    }

    @Override
    @Cacheable(value = "users-tokens", key = "#targetType.name() + '-' + (#targetIds != null ? #targetIds.toString() : 'all')")
    public List<Notificador> findAllNotificadores(TargetType targetType, List<String> targetIds) {
        List<NotificadorProjection> projections = switch (targetType) {
            case GERAL       -> externalRepository.findAllNotificadoresForGeneral();
            case ALUNOS      -> externalRepository.findAllNotificadoresForAlunos();
            case PROFESSORES -> externalRepository.findAllNotificadoresForProfessores();
            case EX_ALUNOS   -> externalRepository.findAllNotificadoresForExAlunos();
            case TURMA, TURMAS -> externalRepository.findAllNotificadoresForTurmas(targetIds);
        };
        return projections.stream()
                .map(p -> new Notificador(p.getId(), p.getEndpoint(), p.getP256dh(), p.getAuth()))
                .toList();
    }

    @Override
    public List<Notificador> findNotificadoresByUserId(String userId) {
        return externalRepository.findNotificadoresByUserId(userId)
                .stream()
                .map(p -> new Notificador(p.getId(), p.getEndpoint(), p.getP256dh(), p.getAuth()))
                .toList();
    }

    @Override
    @Transactional
    public void saveNotificador(String userId, String endpoint, String p256dh, String auth) {
        var user = externalRepository.findById(userId).orElseThrow();

        boolean jaExiste = user.getNotificadores().stream()
                .anyMatch(n -> n.getEndpoint().equals(endpoint));
        if (jaExiste) return;

        var entity = new NotificadorEntity();
        entity.setUser(user);
        entity.setEndpoint(endpoint);
        entity.setP256dh(p256dh);
        entity.setAuth(auth);
        user.getNotificadores().add(entity);
        externalRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteNotificador(String userId, String endpoint) {
        var user = externalRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException()
        );
        user.getNotificadores().removeIf(n -> n.getEndpoint().equals(endpoint));
    }

    @Override
    @Transactional
    public Optional<User> findById(String id) {
        return externalRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    @Cacheable(value = "users-lists", key = "'cursantes-' + #pageable.page()")
    public PageResult<User> findAllCursantes(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAllCursantes(PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    @Cacheable(value = "users-lists", key = "'funcionarios-' + #pageable.page()")
    public PageResult<User> findAllFuncionarios(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findByTipoIn(List.of("PROFESSOR", "SECRETARIA"), PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    @Cacheable(value = "users-lists", key = "'turma-' + #idTurma + '-' + #pageable.page()")
    public PageResult<User> findAllByTurma(String idTurma, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAllPeopleByTurma(idTurma, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    @Cacheable(value = "users-lists", key = "'secretaria-' + #pageable.page()")
    public PageResult<User> findAll(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAll(PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    @Cacheable(value = "users-lists", key = "'name-' + #name.name() + '-' + #pageable.page()")
    public PageResult<User> findAllByName(Name name, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findByNomeContaining(name.name(), PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    public PageResult<User> findAllSecretaria(PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findByTipoIn(List.of("SECRETARIA"), PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    public List<Notificador> findAllNotificadoresSecretariaSolicitation() {
        return externalRepository.findAllNotificadoresForSecretaria()
                .stream()
                .map(p -> new Notificador(p.getId(), p.getEndpoint(), p.getP256dh(), p.getAuth()))
                .toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users-lists", allEntries = true),
            @CacheEvict(value = "users-context", allEntries = true)
    })
    public void delete(String id) {
        externalRepository.logicDelete(id);
    }

    @Override
    @Transactional
    public Optional<User> findByIdAlunos(String id, String id_aluno) {
        return externalRepository.findAlunoByProfessor(id, id_aluno)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    @Cacheable(value = "users-context", key = "'prof-' + #id + '-alunos-' + #pageable.page()")
    public PageResult<User> findAllAlunos(String id, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAllAlunosByProfessor(id, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    @Cacheable(value = "users-context", key = "'prof-' + #id + '-turma-' + #idTurma + '-' + #pageable.page()")
    public PageResult<User> findAllAlunosByTurma(String id, String idTurma, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAlunosByTurmaWhereProfessorHasAccess(id, idTurma, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    public PageResult<User> findAllAlunosByNome(String id, Name name, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAlunosByNameAndProfessor(id, name.name(), PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }

    @Override
    @Transactional
    public Optional<User> findByIdProfessores(String id, String id_professor) {
        return externalRepository.findProfessorIfIsFromStudentTurma(id, id_professor)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    @Cacheable(value = "users-context", key = "'aluno-' + #id + '-profs-' + #pageable.page()")
    public PageResult<User> findAllProfessores(String id, PageRequest pageable) {
        return PaginationAdapter.toDomain(externalRepository.findAllProfessoresByAluno(id, PaginationAdapter.toSpring(pageable))
                .map(mapper::toDomain));
    }
}
