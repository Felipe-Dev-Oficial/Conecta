package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters;

import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryAdapterIntegrationTest {

    @Autowired private UserRepositoryAdapter adapter;
    @Autowired private JpaUserRepository jpaRepository;
    @Autowired private CacheManager cacheManager;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        });
    }

    private UserEntity buildUserEntity(String id, String nome, Tipo tipo) {
        UserEntity e = new UserEntity();
        e.setId(id);
        e.setNome(nome);
        e.setEmail(id + "@etec.sp.gov.br");
        e.setNumero("11987654321");
        e.setSenha("$2a$10$Y566OuOe98ufH8H7RYqRTuS7AJvy5Z6Pg6yJP22WSWzAD0.Z.fE6u");
        e.setTipo(tipo);
        return e;
    }

    @Test
    @Order(1)
    @DisplayName("findAllAlunosByNome deve ignorar Case (LIKE case-insensitive)")
    void findAllByNome_shouldBeCaseInsensitive() {
        var aluno = buildUserEntity("RM1", "Arthur Silva", Tipo.ALUNO);
        jpaRepository.save(aluno);

        var professor = buildUserEntity("PROF-1", "Professor X", Tipo.PROFESSOR);
        jpaRepository.save(professor);

        jdbcTemplate.execute("""
        INSERT INTO turmas (id, curso, modulos, atual, status) 
        VALUES ('TURMA-1', 'DESENVOLVIMENTO_DE_SISTEMAS', 3, 1, 'ON')
        """);

        jdbcTemplate.execute("INSERT INTO aluno_turmas (aluno_id, turma_id) VALUES ('RM1', 'TURMA-1')");
        jdbcTemplate.execute("INSERT INTO professor_turmas (professor_id, turma_id) VALUES ('PROF-1', 'TURMA-1')");

        var result = adapter.findAllAlunosByNome("PROF-1", new Name("ARTHUR"), new PageRequest(0, 10));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getNome().name()).isEqualTo("Arthur Silva");
    }

    @Test
    @Order(2)
    @DisplayName("delete() deve realizar Soft Delete")
    void delete_shouldPerformSoftDelete() {
        jpaRepository.save(buildUserEntity("RM2", "Julia", Tipo.ALUNO));

        adapter.delete("RM2");

        var entity = jpaRepository.findById("RM2").get();
        assertThat(entity.getTipo()).isEqualTo(Tipo.DESATIVADO);
    }

    @Test
    @Order(3)
    @DisplayName("findAllSecretaria() deve listar apenas quem é do tipo SECRETARIA")
    void findAllSecretaria_shouldFilterCorrectly() {
        jpaRepository.save(buildUserEntity("SEC1", "Cida", Tipo.SECRETARIA));
        jpaRepository.save(buildUserEntity("RM3", "Marcos", Tipo.ALUNO));

        var result = adapter.findAllSecretaria(new PageRequest(0, 10));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getTipo()).isEqualTo(Tipo.SECRETARIA);
    }
}