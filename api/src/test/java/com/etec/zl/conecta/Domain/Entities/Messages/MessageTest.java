package com.etec.zl.conecta.Domain.Entities.Messages;

import com.etec.zl.conecta.Domain.Entities.Midia.Midia;
import com.etec.zl.conecta.Domain.ValueObjects.TipoMidia;
import com.etec.zl.conecta.Domain.Exceptions.ProcessingErrorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Message Entity Test")
class MessageTest {

    private static final String SENDER_ID = "user-sender-1";
    private static final String RECEIVER_ID = "user-receiver-2";

    private Message messageSemMidia() {
        return new Message(SENDER_ID, RECEIVER_ID, "Olá!", null);
    }

    private Message messageComMidia() {
        return new Message(
                SENDER_ID, RECEIVER_ID,
                "Veja essa foto",
                new Midia(TipoMidia.FOTO, "https://img.com/foto.jpg")
        );
    }

    @Nested
    @DisplayName("Criação via construtor de negócio")
    class CriacaoNegocio {

        @Test
        @DisplayName("deve gerar UUID e timestamp automaticamente")
        void dadosAutomaticos() {
            var antes = Instant.now();
            var msg = messageSemMidia();

            assertNotNull(msg.getId());
            assertNotNull(msg.getTimestamp());
            assertFalse(msg.getTimestamp().isBefore(antes));
        }

        @Test
        @DisplayName("deve preservar strings de conteúdo e IDs corretamente")
        void camposCorretos() {
            var msg = messageSemMidia();
            assertEquals("Olá!", msg.getContent());
            assertEquals(SENDER_ID, msg.getIdSender());
            assertEquals(RECEIVER_ID, msg.getIdReceiver());
        }

        @Test
        @DisplayName("deve permitir mensagem apenas com mídia (content vazio/null)")
        void apenasMidia() {
            var midia = new Midia(TipoMidia.FOTO, "url");
            assertDoesNotThrow(() -> new Message(SENDER_ID, RECEIVER_ID, null, midia));
            assertDoesNotThrow(() -> new Message(SENDER_ID, RECEIVER_ID, "", midia));
        }
    }

    @Nested
    @DisplayName("Validações de Regra de Negócio")
    class Validacoes {

        @Test
        @DisplayName("deve lançar exceção se content for nulo e mídia for nula")
        void erroTudoNulo() {
            var ex = assertThrows(ProcessingErrorException.class, () ->
                    new Message(SENDER_ID, RECEIVER_ID, null, null)
            );
            assertEquals("Você não pode enviar uma mensagem vazia", ex.getMessage());
        }

        @Test
        @DisplayName("deve lançar exceção se content for vazio e mídia for nula")
        void erroTudoVazio() {
            var ex = assertThrows(ProcessingErrorException.class, () ->
                    new Message(SENDER_ID, RECEIVER_ID, "", null)
            );
            assertEquals("Você não pode enviar uma mensagem vazia", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Criação via construtor completo (Persistência)")
    class CriacaoCompleta {

        @Test
        @DisplayName("deve manter os dados exatamente como fornecidos")
        void persistenciaDados() {
            var id = UUID.randomUUID();
            var ts = Instant.parse("2026-04-27T10:00:00Z");
            var msg = new Message(id, SENDER_ID, RECEIVER_ID, ts, "Conteúdo", null);

            assertEquals(id, msg.getId());
            assertEquals(ts, msg.getTimestamp());
            assertEquals("Conteúdo", msg.getContent());
        }
    }
}