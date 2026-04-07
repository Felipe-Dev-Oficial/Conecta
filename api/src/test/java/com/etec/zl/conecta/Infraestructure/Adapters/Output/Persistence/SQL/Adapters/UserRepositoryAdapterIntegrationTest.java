package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Adapters;

import com.etec.zl.conecta.Domain.Entities.Users.User;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Entities.UserEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories.JpaUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryAdapterIntegrationTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine").withExposedPorts(6379);

    @Autowired private UserRepositoryAdapter adapter;
    @Autowired private JpaUserRepository jpaRepository;
    @Autowired private CacheManager cacheManager;

    private UserEntity buildUserEntity(String id, String nome, Tipo tipo) {
        UserEntity e = new UserEntity();
        e.setId(id);
        e.setNome(nome);
        e.setEmail(id + "@etec.sp.gov.br");
        e.setSenha("$2a$10$Y566OuOe98ufH8H7RYqRTuS7AJvy5Z6Pg6yJP22WSWzAD0.Z.fE6u");
        e.setTipo(tipo);
        return e;
    }

    @Test
    @Order(1)
    @DisplayName("findAllAlunosByNome deve ignorar Case (ILIKE)")
    void findAllByNome_shouldBeCaseInsensitive() {
        jpaRepository.save(buildUserEntity("RM1", "Arthur Silva", Tipo.ALUNO));

        var result = adapter.findAllAlunosByNome("PROF-1", new Name("ARTHUR"), new PageRequest(0, 10));

        assertThat(result.content()).hasSize(1);
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