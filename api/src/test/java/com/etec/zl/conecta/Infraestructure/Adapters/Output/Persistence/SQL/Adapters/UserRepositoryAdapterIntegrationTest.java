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

import java.util.List;

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

    @Test
    @Order(4)
    @DisplayName("saveNotificador() deve salvar um notificador para o usuário")
    void saveNotificador_shouldPersistCorrectly() {
        jpaRepository.save(buildUserEntity("RM4", "Carlos", Tipo.ALUNO));

        adapter.saveNotificador("RM4", "https://fcm.example.com/ep1", "p256dh-1", "auth-1");

        var result = adapter.findNotificadoresByUserId("RM4");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).endpoint()).isEqualTo("https://fcm.example.com/ep1");
        assertThat(result.get(0).p256dh()).isEqualTo("p256dh-1");
        assertThat(result.get(0).auth()).isEqualTo("auth-1");
    }

    @Test
    @Order(5)
    @DisplayName("saveNotificador() não deve duplicar endpoint já existente")
    void saveNotificador_shouldNotDuplicateEndpoint() {
        jpaRepository.save(buildUserEntity("RM5", "Ana", Tipo.ALUNO));

        adapter.saveNotificador("RM5", "https://fcm.example.com/ep2", "p256dh-2", "auth-2");
        adapter.saveNotificador("RM5", "https://fcm.example.com/ep2", "p256dh-2", "auth-2");

        var result = adapter.findNotificadoresByUserId("RM5");
        assertThat(result).hasSize(1);
    }

    @Test
    @Order(6)
    @DisplayName("saveNotificador() deve permitir múltiplos endpoints distintos para o mesmo usuário")
    void saveNotificador_shouldAllowMultipleDistinctEndpoints() {
        jpaRepository.save(buildUserEntity("RM6", "Pedro", Tipo.ALUNO));

        adapter.saveNotificador("RM6", "https://fcm.example.com/ep3", "p256dh-3", "auth-3");
        adapter.saveNotificador("RM6", "https://fcm.example.com/ep4", "p256dh-4", "auth-4");

        var result = adapter.findNotificadoresByUserId("RM6");
        assertThat(result).hasSize(2);
    }

    @Test
    @Order(7)
    @DisplayName("findNotificadoresByUserId() deve retornar lista vazia para usuário sem notificadores")
    void findNotificadoresByUserId_shouldReturnEmptyForUserWithoutNotificadores() {
        jpaRepository.save(buildUserEntity("RM7", "Beatriz", Tipo.ALUNO));

        var result = adapter.findNotificadoresByUserId("RM7");
        assertThat(result).isEmpty();
    }

    @Test
    @Order(8)
    @DisplayName("findAllNotificadores() GERAL deve retornar notificadores de usuários não desativados")
    void findAllNotificadores_geral_shouldExcludeDesativados() {
        jpaRepository.save(buildUserEntity("RM8", "Lucas", Tipo.ALUNO));
        jpaRepository.save(buildUserEntity("RM9", "Ex-Aluno", Tipo.DESATIVADO));

        adapter.saveNotificador("RM8", "https://fcm.example.com/ep5", "p256dh-5", "auth-5");
        adapter.saveNotificador("RM9", "https://fcm.example.com/ep6", "p256dh-6", "auth-6");

        var result = adapter.findAllNotificadores(TargetType.GERAL, List.of());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).endpoint()).isEqualTo("https://fcm.example.com/ep5");
    }

    @Test
    @Order(9)
    @DisplayName("findAllNotificadores() ALUNOS deve retornar apenas notificadores de alunos")
    void findAllNotificadores_alunos_shouldFilterByTipo() {
        jpaRepository.save(buildUserEntity("RM10", "Aluno Teste", Tipo.ALUNO));
        jpaRepository.save(buildUserEntity("PROF-2", "Prof Teste", Tipo.PROFESSOR));

        adapter.saveNotificador("RM10", "https://fcm.example.com/ep7", "p256dh-7", "auth-7");
        adapter.saveNotificador("PROF-2", "https://fcm.example.com/ep8", "p256dh-8", "auth-8");

        var result = adapter.findAllNotificadores(TargetType.ALUNOS, List.of());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).endpoint()).isEqualTo("https://fcm.example.com/ep7");
    }

    @Test
    @Order(10)
    @DisplayName("findAllNotificadores() TURMA deve retornar apenas notificadores de alunos da turma")
    void findAllNotificadores_turma_shouldFilterByTurma() {
        jpaRepository.save(buildUserEntity("RM11", "Aluno Turma A", Tipo.ALUNO));
        jpaRepository.save(buildUserEntity("RM12", "Aluno Turma B", Tipo.ALUNO));

        jdbcTemplate.execute("""
        INSERT INTO turmas (id, curso, modulos, atual, status)
        VALUES ('TURMA-2', 'DESENVOLVIMENTO_DE_SISTEMAS', 3, 1, 'ON')
    """);
        jdbcTemplate.execute("INSERT INTO aluno_turmas (aluno_id, turma_id) VALUES ('RM11', 'TURMA-2')");

        adapter.saveNotificador("RM11", "https://fcm.example.com/ep9",  "p256dh-9",  "auth-9");
        adapter.saveNotificador("RM12", "https://fcm.example.com/ep10", "p256dh-10", "auth-10");

        var result = adapter.findAllNotificadores(TargetType.TURMA, List.of("TURMA-2"));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).endpoint()).isEqualTo("https://fcm.example.com/ep9");
    }
}