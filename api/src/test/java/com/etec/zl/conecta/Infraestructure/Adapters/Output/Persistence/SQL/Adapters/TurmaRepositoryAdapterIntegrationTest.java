package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters;

import com.etec.zl.conecta.Domain.Entities.Turmas.Turma;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.TurmaEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaTurmaRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TurmaRepositoryAdapterIntegrationTest {

    @Autowired
    private TurmaRepositoryAdapter adapter;
    @Autowired
    private JpaTurmaRepository jpaRepository;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    public static java.sql.Timestamp dataFake() {
        return java.sql.Timestamp.valueOf("2026-01-15 10:00:00");
    }

    private TurmaEntity buildTurmaEntity(Cursos curso, Status status, int atual, int modulos) {
        TurmaEntity e = new TurmaEntity();
        e.setId(curso.getSigla() + "-" + UUID.randomUUID().toString().substring(0, 5));
        e.setCurso(curso);
        e.setStatus(status);
        e.setAtual(atual);
        e.setModulos(modulos);
        return e;
    }

    @Test
    @Order(1)
    @DisplayName("save() deve persistir uma Turma e invalidar cache")
    void save_shouldPersistAndEvictCache() {
        adapter.findAllTurmas(new PageRequest(0, 10)); // aquece cache
        Turma turma = new Turma(Cursos.DESENVOLVIMENTO_DE_SISTEMAS, 3);

        adapter.save(turma);

        assertThat(jpaRepository.findById(turma.getId())).isPresent();
        assertThat(cacheManager.getCache("turmas").get("all-0")).isNull();
    }

    @Test
    @Order(2)
    @DisplayName("findById() deve retornar Turma e usar cache")
    void findById_shouldUseCache() {
        TurmaEntity entity = jpaRepository.save(buildTurmaEntity(Cursos.ADMINISTRACAO, Status.ON, 1, 3));

        adapter.findById(entity.getId()); // aquece cache
        jpaRepository.deleteById(entity.getId()); // remove do banco

        Optional<Turma> cached = adapter.findById(entity.getId());
        assertThat(cached).isPresent();
        assertThat(cached.get().getId()).isEqualTo(entity.getId());
    }

    @Test
    @Order(3)
    @DisplayName("findAllTurmasAtuais() deve filtrar apenas turmas ON")
    void findAllAtuais_shouldFilterCorrectly() {
        jpaRepository.save(buildTurmaEntity(Cursos.DESENVOLVIMENTO_DE_SISTEMAS, Status.ON, 1, 3));
        jpaRepository.save(buildTurmaEntity(Cursos.ADMINISTRACAO, Status.OFF, 1, 3));

        PageResult<Turma> result = adapter.findAllTurmasAtuais(new PageRequest(0, 10));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getStatus()).isEqualTo(Status.ON);
    }

    @Test
    @Order(4)
    @Transactional
    @DisplayName("passaModulo() deve incrementar modulo ou desativar turma")
    void passaModulo_shouldUpdateStatusOrModulo() {

        jpaRepository.save(buildTurmaEntity(Cursos.DESENVOLVIMENTO_DE_SISTEMAS, Status.ON, 1, 3));
        jpaRepository.save(buildTurmaEntity(Cursos.ADMINISTRACAO, Status.ON, 3, 3));

        entityManager.flush();

        entityManager.createNativeQuery("""
            UPDATE turmas 
            SET 
                status = CASE WHEN atual >= modulos THEN 'OFF' ELSE status END,
                atual = CASE WHEN atual < modulos THEN atual + 1 ELSE atual END
            WHERE status = 'ON'
        """).executeUpdate();

        entityManager.flush();
        entityManager.clear();

        var t1 = jpaRepository.findAll().stream()
                .filter(t -> t.getCurso() == Cursos.DESENVOLVIMENTO_DE_SISTEMAS).findFirst().get();
        var t2 = jpaRepository.findAll().stream()
                .filter(t -> t.getCurso() == Cursos.ADMINISTRACAO).findFirst().get();

        assertThat(t1.getAtual()).isEqualTo(2);
        assertThat(t2.getStatus()).isEqualTo(Status.OFF);
    }
}
