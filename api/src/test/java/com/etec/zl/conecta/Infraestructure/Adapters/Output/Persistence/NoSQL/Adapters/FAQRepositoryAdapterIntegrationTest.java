package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Domain.Entities.FAQs.FAQ;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.Prioridade;
import com.etec.zl.conecta.Domain.ValueObjects.StatusFAQ;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.FAQEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoFAQRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FAQRepositoryAdapterIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @Autowired
    private FAQRepositoryAdapter adapter;

    @Autowired
    private MongoFAQRepository mongoRepository;

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
    // Helper
    // -------------------------------------------------------

    private FAQEntity buildEntity(StatusFAQ status, String question, String answer) {
        FAQEntity e = new FAQEntity();
        e.setId(UUID.randomUUID());
        e.setAuthorId(UUID.randomUUID().toString());
        e.setStatusFAQ(status);
        e.setQuestion(question);
        e.setAnswer(answer);
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        e.setRelevance(Prioridade.MEDIA);
        return e;
    }

    private FAQ buildDomain(StatusFAQ status, String question, String answer) {
        return new FAQ(
                UUID.randomUUID(),
                question,
                answer,
                "author-123",
                status,
                Instant.now(),
                Instant.now(),
                Prioridade.MEDIA
        );
    }

    // -------------------------------------------------------
    // save()
    // -------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("save() deve persistir FAQ no MongoDB")
    void save_shouldPersistFAQ() {
        FAQ faq = buildDomain(StatusFAQ.PUBLICADO, "Pergunta 1", "Resposta 1");

        adapter.save(faq);

        Optional<FAQEntity> found = mongoRepository.findById(faq.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(faq.getId());
    }

    @Test
    @Order(2)
    @DisplayName("save() deve invalidar todos os caches de faqs")
    void save_shouldEvictFaqsCache() {
        FAQ faq = buildDomain(StatusFAQ.PUBLICADO, "Pergunta", "Resposta");

        // Aquece cache
        adapter.getAll(new PageRequest(0, 10));

        // Salva e invalida
        adapter.save(faq);

        var cache = cacheManager.getCache("faqs");
        assertThat(cache).isNotNull();
        // Cache deve ter sido limpo (allEntries = true)
        assertThat(cache.get("page-0")).isNull();
    }

    // -------------------------------------------------------
    // getAllActives()
    // -------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("getAllActives() deve retornar apenas FAQs com status PUBLICADO")
    void getAllActives_shouldReturnOnlyPublished() {
        mongoRepository.save(buildEntity(StatusFAQ.PUBLICADO, "Q1", "A1"));
        mongoRepository.save(buildEntity(StatusFAQ.PUBLICADO, "Q2", "A2"));
        mongoRepository.save(buildEntity(StatusFAQ.RASCUNHO, "Q3", "A3")); // não deve aparecer
        mongoRepository.save(buildEntity(StatusFAQ.APAGADO, "Q4", "A4")); // não deve aparecer

        PageResult<FAQ> result = adapter.getAllActives(new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
        assertThat(result.content())
                .extracting(FAQ::getStatusFAQ)
                .allMatch(s -> s.equals(StatusFAQ.PUBLICADO));
    }

    @Test
    @Order(4)
    @DisplayName("getAllActives() deve retornar vazio quando não há FAQs publicadas")
    void getAllActives_shouldReturnEmpty_whenNonePublished() {
        mongoRepository.save(buildEntity(StatusFAQ.RASCUNHO, "Q1", "A1"));

        PageResult<FAQ> result = adapter.getAllActives(new PageRequest(0, 10));

        assertThat(result.content()).isEmpty();
    }

    @Test
    @Order(5)
    @DisplayName("getAllActives() deve paginar corretamente")
    void getAllActives_shouldPaginateCorrectly() {
        for (int i = 1; i <= 5; i++) {
            mongoRepository.save(buildEntity(StatusFAQ.PUBLICADO, "Q" + i, "A" + i));
        }

        PageResult<FAQ> page0 = adapter.getAllActives(new PageRequest(0, 3));
        PageResult<FAQ> page1 = adapter.getAllActives(new PageRequest(1, 3));

        assertThat(page0.content()).hasSize(3);
        assertThat(page1.content()).hasSize(2);
        assertThat(page0.totalElements()).isEqualTo(5);
    }

    // -------------------------------------------------------
    // getAll()
    // -------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("getAll() deve retornar todos os FAQs independente do status")
    void getAll_shouldReturnAllFAQs() {
        mongoRepository.save(buildEntity(StatusFAQ.PUBLICADO, "Q1", "A1"));
        mongoRepository.save(buildEntity(StatusFAQ.RASCUNHO, "Q2", "A2"));
        mongoRepository.save(buildEntity(StatusFAQ.APAGADO, "Q3", "A3"));

        PageResult<FAQ> result = adapter.getAll(new PageRequest(0, 10));

        assertThat(result.content()).hasSize(3);
    }

    @Test
    @Order(7)
    @DisplayName("getAll() deve usar cache na segunda chamada da mesma página")
    void getAll_shouldUseCache_onSecondCall() {
        mongoRepository.save(buildEntity(StatusFAQ.PUBLICADO, "Q1", "A1"));

        PageResult<FAQ> first = adapter.getAll(new PageRequest(0, 10));
        mongoRepository.deleteAll(); // limpa banco mas mantém cache

        PageResult<FAQ> cached = adapter.getAll(new PageRequest(0, 10));

        assertThat(cached.content()).hasSize(first.content().size());
    }

    @Test
    @Order(8)
    @DisplayName("getAll() deve paginar corretamente")
    void getAll_shouldPaginateCorrectly() {
        for (int i = 1; i <= 6; i++) {
            mongoRepository.save(buildEntity(StatusFAQ.PUBLICADO, "Q" + i, "A" + i));
        }

        PageResult<FAQ> page0 = adapter.getAll(new PageRequest(0, 4));
        PageResult<FAQ> page1 = adapter.getAll(new PageRequest(1, 4));

        assertThat(page0.content()).hasSize(4);
        assertThat(page1.content()).hasSize(2);
    }

    // -------------------------------------------------------
    // getById()
    // -------------------------------------------------------

    @Test
    @Order(9)
    @DisplayName("getById() deve retornar FAQ existente")
    void getById_shouldReturnFAQ_whenExists() {
        FAQEntity entity = buildEntity(StatusFAQ.PUBLICADO, "Pergunta", "Resposta");
        mongoRepository.save(entity);

        Optional<FAQ> result = adapter.getById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(entity.getId());
    }

    @Test
    @Order(10)
    @DisplayName("getById() deve retornar empty quando não existe")
    void getById_shouldReturnEmpty_whenNotFound() {
        Optional<FAQ> result = adapter.getById(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    @Order(11)
    @DisplayName("getById() deve usar cache na segunda chamada")
    void getById_shouldUseCache_onSecondCall() {
        FAQEntity entity = buildEntity(StatusFAQ.PUBLICADO, "Pergunta", "Resposta");
        mongoRepository.save(entity);

        adapter.getById(entity.getId()); // aquece cache
        mongoRepository.deleteById(entity.getId()); // remove do banco

        Optional<FAQ> cached = adapter.getById(entity.getId());
        assertThat(cached).isPresent(); // ainda no cache
    }

    @Test
    @Order(12)
    @DisplayName("getById() deve retornar FAQ com dados corretos")
    void getById_shouldReturnCorrectData() {
        FAQEntity entity = buildEntity(StatusFAQ.PUBLICADO, "Qual o prazo?", "30 dias.");
        mongoRepository.save(entity);

        Optional<FAQ> result = adapter.getById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getQuestion()).isEqualTo("Qual o prazo?");
        assertThat(result.get().getAnswer()).isEqualTo("30 dias.");
    }
}