package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Adapters;

import com.etec.zl.conecta.Domain.Entities.Messages.Message;
import com.etec.zl.conecta.Domain.ValueObjects.Content;
import com.etec.zl.conecta.Domain.ValueObjects.PageRequest;
import com.etec.zl.conecta.Domain.ValueObjects.PageResult;
import com.etec.zl.conecta.Domain.ValueObjects.SliceResult;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Entities.MessageEntity;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.NoSQL.Repositories.MongoMessageRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageRepositoryAdapterIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @Autowired
    private MessageRepositoryAdapter adapter;

    @Autowired
    private MongoMessageRepository mongoRepository;

    @Autowired
    private CacheManager cacheManager;

    private static final String USER_A = UUID.randomUUID().toString();
    private static final String USER_B = UUID.randomUUID().toString();
    private static final String USER_C = UUID.randomUUID().toString();

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

    private MessageEntity buildMessage(String senderId, String receiverId) {
        MessageEntity e = new MessageEntity();
        e.setId(UUID.randomUUID());
        e.setIdSender(senderId);
        e.setIdReceiver(receiverId);
        e.setContent(new Content("Olá!"));
        e.setTimestamp(Instant.now());
        return e;
    }

    private Message buildDomain(String sender, String receiver, String text) {
        return new Message(
                UUID.randomUUID(),
                sender,
                receiver,
                Instant.now(),
                new Content(text),
                null
        );
    }

    // -------------------------------------------------------
    // save()
    // -------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("save() deve persistir mensagem no MongoDB")
    void save_shouldPersistMessage() {
        Message message = buildDomain(USER_A, USER_B, "Olá!");

        adapter.save(message);

        assertThat(mongoRepository.findAll()).hasSize(1);
    }

    @Test
    @Order(2)
    @DisplayName("save() deve invalidar o cache de messages")
    void save_shouldEvictMessagesCache() {
        // Aquece cache
        adapter.ListarMensagens(USER_A, USER_B, new PageRequest(0, 10));

        Message message = buildDomain(USER_A, USER_B, "Nova mensagem");
        adapter.save(message);

        var cache = cacheManager.getCache("messages");
        assertThat(cache).isNotNull();

        String cacheKey = (USER_A.compareTo(USER_B) < 0 ? USER_A + "-" + USER_B : USER_B + "-" + USER_A) + "-0";
        assertThat(cache.get(cacheKey)).isNull();
    }

    // -------------------------------------------------------
    // ListarMensagens()
    // -------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("ListarMensagens() deve retornar mensagens entre dois usuários")
    void listarMensagens_shouldReturnChatBetweenTwoUsers() {
        mongoRepository.save(buildMessage(USER_A, USER_B));
        mongoRepository.save(buildMessage(USER_B, USER_A));
        mongoRepository.save(buildMessage(USER_A, USER_C)); // não deve aparecer

        PageResult<Message> result = adapter.ListarMensagens(USER_A, USER_B, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
        assertThat(result.content())
                .allMatch(m -> (m.getIdSender().equals(USER_A) && m.getIdReceiver().equals(USER_B))
                        || (m.getIdSender().equals(USER_B) && m.getIdReceiver().equals(USER_A)));
    }

    @Test
    @Order(4)
    @DisplayName("ListarMensagens() deve retornar vazio quando não há mensagens entre os usuários")
    void listarMensagens_shouldReturnEmpty_whenNoMessages() {
        PageResult<Message> result = adapter.ListarMensagens(USER_A, USER_B, new PageRequest(0, 10));
        assertThat(result.content()).isEmpty();
    }

    @Test
    @Order(5)
    @DisplayName("ListarMensagens() deve usar cache na segunda chamada")
    void listarMensagens_shouldUseCache_onSecondCall() {
        mongoRepository.save(buildMessage(USER_A, USER_B));

        adapter.ListarMensagens(USER_A, USER_B, new PageRequest(0, 10)); // aquece
        mongoRepository.deleteAll();

        PageResult<Message> cached = adapter.ListarMensagens(USER_A, USER_B, new PageRequest(0, 10));
        assertThat(cached.content()).hasSize(1);
    }

    @Test
    @Order(6)
    @DisplayName("ListarMensagens() deve paginar corretamente")
    void listarMensagens_shouldPaginate() {
        for (int i = 0; i < 5; i++) {
            mongoRepository.save(buildMessage(USER_A, USER_B));
        }

        PageResult<Message> page0 = adapter.ListarMensagens(USER_A, USER_B, new PageRequest(0, 3));
        PageResult<Message> page1 = adapter.ListarMensagens(USER_A, USER_B, new PageRequest(1, 3));

        assertThat(page0.content()).hasSize(3);
        assertThat(page1.content()).hasSize(2);
    }

    // -------------------------------------------------------
    // ListarMensagensSecretaria()
    // -------------------------------------------------------

    @Test
    @Order(7)
    @DisplayName("ListarMensagensSecretaria() deve retornar mensagens entre secretaria e usuário")
    void listarMensagensSecretaria_shouldReturnMessages() {
        mongoRepository.save(buildMessage(USER_A, USER_B));
        mongoRepository.save(buildMessage(USER_B, USER_A));
        mongoRepository.save(buildMessage(USER_A, USER_C)); // não deve aparecer

        PageResult<Message> result = adapter.ListarMensagensSecretaria(USER_A, USER_B, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
    }

    @Test
    @Order(8)
    @DisplayName("ListarMensagensSecretaria() deve usar cache com chave ordenada corretamente")
    void listarMensagensSecretaria_shouldUseSortedCacheKey() {
        mongoRepository.save(buildMessage(USER_B, USER_A)); // invertido

        adapter.ListarMensagensSecretaria(USER_A, USER_B, new PageRequest(0, 10));
        mongoRepository.deleteAll();

        PageResult<Message> cached = adapter.ListarMensagensSecretaria(USER_B, USER_A, new PageRequest(0, 10));
        assertThat(cached.content()).hasSize(1);
    }

    // -------------------------------------------------------
    // contatos()
    // -------------------------------------------------------

    @Test
    @Order(9)
    @DisplayName("contatos() deve retornar IDs distintos de contatos de um usuário")
    void contatos_shouldReturnDistinctContactIds() {
        mongoRepository.save(buildMessage(USER_A, USER_B));
        mongoRepository.save(buildMessage(USER_A, USER_B)); // duplicada
        mongoRepository.save(buildMessage(USER_C, USER_A));
        mongoRepository.save(buildMessage(USER_A, USER_C));

        SliceResult<String> result = adapter.contatos(USER_A, new PageRequest(0, 10));

        assertThat(result.content()).hasSize(2);
        assertThat(result.content()).containsExactlyInAnyOrder(USER_B, USER_C);
    }

    @Test
    @Order(10)
    @DisplayName("contatos() deve retornar vazio quando usuário não tem mensagens")
    void contatos_shouldReturnEmpty_whenNoMessages() {
        SliceResult<String> result = adapter.contatos(USER_A, new PageRequest(0, 10));
        assertThat(result.content()).isEmpty();
    }

    @Test
    @Order(11)
    @DisplayName("contatos() deve usar cache na segunda chamada")
    void contatos_shouldUseCache_onSecondCall() {
        mongoRepository.save(buildMessage(USER_A, USER_B));

        adapter.contatos(USER_A, new PageRequest(0, 10)); // aquece
        mongoRepository.deleteAll();

        SliceResult<String> cached = adapter.contatos(USER_A, new PageRequest(0, 10));
        assertThat(cached.content()).hasSize(1);
    }

    @Test
    @Order(12)
    @DisplayName("contatos() não deve incluir o próprio usuário como contato")
    void contatos_shouldNotIncludeSelfAsContact() {
        mongoRepository.save(buildMessage(USER_A, USER_B));
        mongoRepository.save(buildMessage(USER_B, USER_A));

        SliceResult<String> result = adapter.contatos(USER_A, new PageRequest(0, 10));

        assertThat(result.content()).doesNotContain(USER_A);
    }
}