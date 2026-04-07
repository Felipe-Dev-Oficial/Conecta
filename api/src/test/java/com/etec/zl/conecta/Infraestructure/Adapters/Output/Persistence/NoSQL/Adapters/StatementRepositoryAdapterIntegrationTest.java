package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Application.DTOs.Statements.DTOLeitura;
import com.etec.zl.conecta.Domain.Entities.Statements.Statement;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.StatementEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoStatementRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatementRepositoryAdapterIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @Autowired
    private StatementRepositoryAdapter adapter;

    @Autowired
    private MongoStatementRepository mongoRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        mongoRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private StatementEntity buildEntity(Status status, TargetType targetType, List<String> targetIds, Prioridade priority) {
        StatementEntity e = new StatementEntity();
        e.setId(UUID.randomUUID());
        e.setStatus(status);
        e.setPriority(priority);
        e.setTimestamp(Instant.now());
        var targetVO = new TargetVO(targetType, targetIds);
        e.setTargetVO(targetVO);
        return e;
    }

    private Statement buildDomain(Status status, TargetType targetType, List<String> targetIds) {
        return new Statement(
                UUID.randomUUID(),
                "sender-id",
                new Content("Título do Anúncio"),
                Instant.now(),
                new Content("Conteúdo do Anúncio"),
                null,
                Prioridade.BAIXA,
                false,
                status,
                new TargetVO(targetType, targetIds)
        );
    }

    // -------------------------------------------------------
    // save()
    // -------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("save() deve persistir um Statement no MongoDB")
    void save_shouldPersistStatement() {
        Statement statement = buildDomain(Status.ON, TargetType.GERAL, List.of());

        adapter.save(statement);

        Optional<StatementEntity> found = mongoRepository.findById(statement.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(statement.getId());
    }

    @Test
    @Order(2)
    @DisplayName("save() deve invalidar o cache de statements ao salvar")
    void save_shouldEvictCache() {
        Statement statement = buildDomain(Status.ON, TargetType.GERAL, List.of());

        // Popula cache
        adapter.findById(statement.getId());

        // Salva e invalida
        adapter.save(statement);

        var cache = cacheManager.getCache("statements");
        assertThat(cache).isNotNull();
        assertThat(cache.get(statement.getId())).isNull();
    }

    // -------------------------------------------------------
    // findById()
    // -------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("findById() deve retornar Statement existente")
    void findById_shouldReturnStatement_whenExists() {
        StatementEntity entity = buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA);
        mongoRepository.save(entity);

        Optional<Statement> result = adapter.findById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(entity.getId());
    }

    @Test
    @Order(4)
    @DisplayName("findById() deve retornar empty quando não existe")
    void findById_shouldReturnEmpty_whenNotFound() {
        Optional<Statement> result = adapter.findById(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    @Order(5)
    @DisplayName("findById() deve usar cache na segunda chamada")
    void findById_shouldUseCache_onSecondCall() {
        StatementEntity entity = buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA);
        mongoRepository.save(entity);

        adapter.findById(entity.getId()); // popula cache
        mongoRepository.deleteById(entity.getId()); // remove do banco

        // Cache ainda retorna
        Optional<Statement> cached = adapter.findById(entity.getId());
        assertThat(cached).isPresent();
    }

    // -------------------------------------------------------
    // findAllStatements()
    // -------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("findAllStatements() deve retornar todos os statements paginados")
    void findAllStatements_shouldReturnAll() {
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.OFF, TargetType.ALUNOS, List.of(), Prioridade.MEDIA));

        PageResult<Statement> result = adapter.findAllStatements(new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    @Order(7)
    @DisplayName("findAllStatements() deve retornar lista vazia quando não há registros")
    void findAllStatements_shouldReturnEmpty_whenNone() {
        PageResult<Statement> result = adapter.findAllStatements(new PageRequest(0, 10));
        assertThat(result.content()).isEmpty();
    }

    // -------------------------------------------------------
    // findGeneralStatements()
    // -------------------------------------------------------

    @Test
    @Order(8)
    @DisplayName("findGeneralStatements() deve retornar apenas statements GERAL e com status ON")
    void findGeneralStatements_shouldReturnOnlyGeneralAndActive() {
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.ALUNOS, List.of(), Prioridade.MEDIA));
        mongoRepository.save(buildEntity(Status.OFF, TargetType.GERAL, List.of(), Prioridade.ALTA));

        PageResult<Statement> result = adapter.findGeneralStatements(new PageRequest(0, 10));

        assertThat(result.content()).hasSize(1);
    }

    // -------------------------------------------------------
    // findStatements()
    // -------------------------------------------------------

    @Test
    @Order(9)
    @DisplayName("findStatements() para ALUNO deve retornar GERAL, ALUNOS e TURMA correspondente")
    void findStatements_forAluno_shouldReturnCorrectStatements() {
        String turmaId = UUID.randomUUID().toString();

        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.ALUNOS, List.of(), Prioridade.MEDIA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.TURMA, List.of(turmaId), Prioridade.ALTA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.PROFESSORES, List.of(), Prioridade.URGENTE));

        DTOLeitura dto = new DTOLeitura(Tipo.ALUNO, List.of(turmaId));
        PageResult<Statement> result = adapter.findStatements(dto, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(3);
    }

    @Test
    @Order(10)
    @DisplayName("findStatements() para PROFESSOR deve retornar GERAL e PROFESSORES")
    void findStatements_forProfessor_shouldReturnCorrectStatements() {
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.PROFESSORES, List.of(), Prioridade.MEDIA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.ALUNOS, List.of(), Prioridade.ALTA));

        DTOLeitura dto = new DTOLeitura(Tipo.PROFESSOR, null);
        PageResult<Statement> result = adapter.findStatements(dto, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
    }

    @Test
    @Order(11)
    @DisplayName("findStatements() para EX_ALUNO deve retornar GERAL e EX_ALUNOS")
    void findStatements_forExAluno_shouldReturnCorrectStatements() {
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.EX_ALUNOS, List.of(), Prioridade.MEDIA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.ALUNOS, List.of(), Prioridade.ALTA));

        DTOLeitura dto = new DTOLeitura(Tipo.DESATIVADO, null);
        PageResult<Statement> result = adapter.findStatements(dto, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
    }

    @Test
    @Order(12)
    @DisplayName("findStatements() deve respeitar ordenação por priority desc")
    void findStatements_shouldBeOrderedByPriorityDesc() {
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.ALTA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.MEDIA));

        PageResult<Statement> result = adapter.findGeneralStatements(new PageRequest(0, 10));

        List<Statement> content = result.content();
        assertThat(content.get(0).getPriority().getPeso()).isGreaterThanOrEqualTo(content.get(1).getPriority().getPeso());
        assertThat(content.get(1).getPriority().getPeso()).isGreaterThanOrEqualTo(content.get(2).getPriority().getPeso());
    }

    @Test
    @Order(13)
    @DisplayName("findStatements() para ALUNO sem turmas deve retornar GERAL e ALUNOS")
    void findStatements_forAluno_withNoTurmas_shouldReturnGeneralAndAlunos() {
        mongoRepository.save(buildEntity(Status.ON, TargetType.GERAL, List.of(), Prioridade.BAIXA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.ALUNOS, List.of(), Prioridade.MEDIA));
        mongoRepository.save(buildEntity(Status.ON, TargetType.TURMA, List.of("turma-xyz"), Prioridade.ALTA));

        DTOLeitura dto = new DTOLeitura(Tipo.ALUNO, List.of());
        PageResult<Statement> result = adapter.findStatements(dto, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
    }

    @Test
    @Order(14)
    @DisplayName("findStatements() deve retornar vazio quando não há statements ativos")
    void findStatements_shouldReturnEmpty_whenNoActiveStatements() {
        mongoRepository.save(buildEntity(Status.OFF, TargetType.GERAL, List.of(), Prioridade.BAIXA));

        DTOLeitura dto = new DTOLeitura(Tipo.PROFESSOR, null);
        PageResult<Statement> result = adapter.findStatements(dto, new PageRequest(0, 10));

        assertThat(result.content()).isEmpty();
    }
}