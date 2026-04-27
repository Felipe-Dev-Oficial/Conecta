package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Domain.Entities.Solicitations.Solicitation;
import com.etec.zl.conecta.Domain.ValueObjects.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.SolicitationEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoSolicitationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class SolicitationRepositoryAdapterIntegrationTest {

    @Autowired private SolicitationRepositoryAdapter adapter;
    @Autowired private MongoSolicitationRepository mongoRepository;

    private final String USER_A = UUID.randomUUID().toString();
    private final String USER_B = UUID.randomUUID().toString();

    private static final List<String> NOMES_LUCAS = List.of(
            "Lucas Alves", "Lucas Borges", "Lucas Carmo", "Lucas Doria", "Lucas Esteves"
    );

    @BeforeEach
    void setUp() {
        mongoRepository.deleteAll();
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    /**
     * Constrói uma SolicitationEntity diretamente para inserção no MongoDB.
     * Usa setIdSoliciter() que é o campo real da entidade, mapeado pelo
     * MongoSolicitationRepository.findBySolicitatorId().
     */
    private SolicitationEntity buildEntity(String soliciterId, String nome, TypeRequirement type) {
        SolicitationEntity e = new SolicitationEntity();
        e.setId(UUID.randomUUID());
        e.setIdSoliciter(soliciterId);
        e.setNome(nome);
        e.setEmailSoliciter(soliciterId + "@email.com");
        e.setTypeRequirement(type);
        e.setOtherRequirement(type == TypeRequirement.OUTRO ? "Descrição livre" : null);
        e.setSolved(false);
        e.setIdCursos(List.of("curso-1"));
        e.setCreatedAt(Instant.now());
        return e;
    }

    private Solicitation buildDomain(String soliciterId, String nome, TypeRequirement type) {
        return new Solicitation(
                type,
                type == TypeRequirement.OUTRO ? "Descrição livre" : null,
                soliciterId,
                new Name(nome),
                new Email(soliciterId + "@email.com"),
                List.of("curso-1")
        );
    }

    // -------------------------------------------------------
    // saveSolicitation()
    // -------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("save() deve persistir solicitação no MongoDB")
    void save_shouldPersistSolicitation() {
        adapter.saveSolicitation(buildDomain(USER_A, "Joao Silva", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));
        assertThat(mongoRepository.findAll()).hasSize(1);
    }

    @Test
    @Order(2)
    @DisplayName("save() deve persistir os campos corretamente")
    void save_shouldPersistFieldsCorrectly() {
        adapter.saveSolicitation(buildDomain(USER_A, "Maria Souza", TypeRequirement.OUTRO));

        SolicitationEntity saved = mongoRepository.findAll().get(0);
        assertThat(saved.getIdSoliciter()).isEqualTo(USER_A);
        assertThat(saved.getNome()).isEqualTo("Maria Souza");
        assertThat(saved.getTypeRequirement()).isEqualTo(TypeRequirement.OUTRO);
        assertThat(saved.isSolved()).isFalse();
    }

    // -------------------------------------------------------
    // getSolicitationById()
    // -------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("getSolicitationById() deve retornar a solicitação correta pelo id")
    void getSolicitationById_shouldReturnCorrectSolicitation() {
        SolicitationEntity entity = buildEntity(USER_A, "Joao Silva", TypeRequirement.DECLARAÇÃO_DE_MATRICULA);
        mongoRepository.save(entity);

        Optional<Solicitation> result = adapter.getSolicitationById(entity.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getIdSoliciter()).isEqualTo(USER_A);
    }

    @Test
    @Order(4)
    @DisplayName("getSolicitationById() deve retornar Optional vazio quando id não existe")
    void getSolicitationById_shouldReturnEmpty_whenNotFound() {
        Optional<Solicitation> result = adapter.getSolicitationById(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------
    // getSolicitationsByUser()
    // -------------------------------------------------------

    @Test
    @Order(5)
    @DisplayName("getSolicitationsByUser() deve retornar apenas solicitações do usuário informado")
    void getSolicitationsByUser_shouldReturnOnlyForUser() {
        mongoRepository.save(buildEntity(USER_A, "Joao Silva", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));
        mongoRepository.save(buildEntity(USER_A, "Joao Silva", TypeRequirement.OUTRO));
        mongoRepository.save(buildEntity(USER_B, "Maria Souza", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));

        PageResult<Solicitation> result = adapter.getSolicitationsByUser(USER_A, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).allMatch(s -> s.getIdSoliciter().equals(USER_A));
    }

    @Test
    @Order(6)
    @DisplayName("getSolicitationsByUser() deve retornar vazio quando usuário não tem solicitações")
    void getSolicitationsByUser_shouldReturnEmpty_whenNoSolicitations() {
        PageResult<Solicitation> result = adapter.getSolicitationsByUser(USER_A, new PageRequest(0, 10));
        assertThat(result.content()).isEmpty();
    }

    @Test
    @Order(7)
    @DisplayName("getSolicitationsByUser() deve paginar corretamente")
    void getSolicitationsByUser_shouldPaginate() {
        for (String nome : NOMES_LUCAS) {
            mongoRepository.save(buildEntity(USER_A, nome, TypeRequirement.DECLARAÇÃO_DE_MATRICULA));
        }

        PageResult<Solicitation> page0 = adapter.getSolicitationsByUser(USER_A, new PageRequest(0, 3));
        PageResult<Solicitation> page1 = adapter.getSolicitationsByUser(USER_A, new PageRequest(1, 3));

        assertThat(page0.content()).hasSize(3);
        assertThat(page1.content()).hasSize(2);
    }

    // -------------------------------------------------------
    // getSolicitationsBySearch()
    // -------------------------------------------------------

    @Test
    @Order(8)
    @DisplayName("getSolicitationsBySearch() deve retornar por userId quando há correspondência exata de id")
    void getSolicitationsBySearch_shouldReturnByUserId() {
        mongoRepository.save(buildEntity(USER_A, "Joao Silva", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));
        mongoRepository.save(buildEntity(USER_B, "Maria Souza", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));

        // Busca pelo id exato de USER_A — deve retornar via findBySolicitatorId
        PageResult<Solicitation> result = adapter.getSolicitationsBySearch(USER_A, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(1);
        assertThat(result.content()).allMatch(s -> s.getIdSoliciter().equals(USER_A));
    }

    @Test
    @Order(9)
    @DisplayName("getSolicitationsBySearch() deve retornar solicitações cujo nome começa com o prefixo")
    void getSolicitationsBySearch_shouldReturnMatchingByNamePrefix() {
        mongoRepository.save(buildEntity(USER_A, "Ana Paula", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));
        mongoRepository.save(buildEntity(USER_A, "Ana Clara", TypeRequirement.OUTRO));
        mongoRepository.save(buildEntity(USER_B, "Bruno Oliveira", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));

        // "Ana" não casa nenhum idSoliciter, então cai no fallback por nome
        PageResult<Solicitation> result = adapter.getSolicitationsBySearch("Ana", new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).allMatch(s -> s.getNome().name().startsWith("Ana"));
    }

    @Test
    @Order(10)
    @DisplayName("getSolicitationsBySearch() deve retornar vazio quando nenhum nome corresponde ao prefixo")
    void getSolicitationsBySearch_shouldReturnEmpty_whenNoMatch() {
        mongoRepository.save(buildEntity(USER_A, "Carlos Lima", TypeRequirement.DECLARAÇÃO_DE_MATRICULA));

        PageResult<Solicitation> result = adapter.getSolicitationsBySearch("Xyz", new PageRequest(0, 10));

        assertThat(result.content()).isEmpty();
    }

    @Test
    @Order(11)
    @DisplayName("getSolicitationsBySearch() deve paginar corretamente pelo nome")
    void getSolicitationsBySearch_shouldPaginate() {
        for (String nome : NOMES_LUCAS) {
            mongoRepository.save(buildEntity(USER_A, nome, TypeRequirement.DECLARAÇÃO_DE_MATRICULA));
        }

        PageResult<Solicitation> page0 = adapter.getSolicitationsBySearch("Lucas", new PageRequest(0, 3));
        PageResult<Solicitation> page1 = adapter.getSolicitationsBySearch("Lucas", new PageRequest(1, 3));

        assertThat(page0.content()).hasSize(3);
        assertThat(page1.content()).hasSize(2);
    }
}